package com.henry.kotlin.coroutine

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.getOrSet
import kotlin.concurrent.thread
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * step1: registering or run
 * step2: run coroutines
 *  2.1: registering or run code inside coroutines
 *  2.2: run child coroutines
 *
 * Runblocking, CoroutineDispatcher=BlockingEventLoop.queue(Continuation)
 * 同一个coroutineScope instance下面的所有job才受cancel相互影响。
 * 例如: 下面task1,2,3默认继承当前的coroutineScope(IO),所以task1出异常被cancel会导致task1的children,以及parent cancel. parent被
 * cancel会导致他的children task2,3被cancel，以此类推。
 * runBlocking(){
 *   job1 = CoroutineScope(Dispatchers.IO).async{...}
 *
 *   job2 = CoroutineScope(Dispatchers.IO).async{...}
 *
 *   job3 = CoroutineScope(Dispatchers.IO).async{
 *      async{ task1}
 *      async{ task2}
 *      async{ task3}
 *   }
 * }
 * 但是job1,job2,job3之间互不干涉，只要时间足够，job1,job2会自动继续执行直至完成。
 *
 * 两个点：跨协程 和 跨线程, 2个变量？
 *
 */
//jvm args: -Dkotlinx.coroutines.debug
fun main() {
    log("all start")

    for (t in 0..1) {
        var t = thread(true) {
            log("thread starts $t")
            runBlocking(SHRequestContext2(mutableMapOf("name" to "someone else $t...."))) {
                CoroutineScope(Dispatchers.IO + SHRequestContext2.scopeCurrent()).async {
                    log("${SHRequestContext2.scopeCurrent().hashCode()} job0 before delay get from threadcontext:${SHRequestContext2.get("name")}")
                    val job1 = CoroutineScope(Dispatchers.IO + SHRequestContext2.scopeCurrent()).launch {
                        log("${SHRequestContext2.scopeCurrent().hashCode()} job1 get from threadcontext:${SHRequestContext2.get("name")}")
                        SHRequestContext2.put("name", "henryliang $t")
                        if (Math.random() > 0.5) throw Exception("${SHRequestContext2.scopeCurrent().hashCode()} job1 running into error")
                    }

                    val job2 = CoroutineScope(Dispatchers.IO + SHRequestContext2.scopeCurrent(mutableMapOf("age" to "88"))).launch {
                        log("${SHRequestContext2.scopeCurrent().hashCode()} job2 get from threadcontext age=:${SHRequestContext2.get("age")}")
                        //Continuation 1
                        for (id in 0..1000000000) {
                            if (id % 1000000000 == 0) {
                                if (isActive) log("${SHRequestContext2.scopeCurrent().hashCode()} job2.root:$id")
                            }
                        }
                    }

                    //NOTE: Didn't pass as context, so nothing will be there
                    val job3 = CoroutineScope(Dispatchers.IO + SHRequestContext2.scopeCurrent()).launch {
                        SHRequestContext2.put("gender", "male")
                        log("${SHRequestContext2.scopeCurrent().hashCode()} job3 get from threadcontext:${SHRequestContext2.get("name")}")
                        log("${SHRequestContext2.scopeCurrent().hashCode()} job3 get from threadcontext:gender=${SHRequestContext2.get("gender")}")
                        SHRequestContext2.put("name", "henryliang33333 $t")
                        //Continuation 1
                        launch {
                            for (id in 0..1000000) {
                                if (id % 1000000 == 0) {
                                    if (isActive) log("${SHRequestContext2.scopeCurrent().hashCode()} job3.a:$id")
                                }
                            }
                            log("${SHRequestContext2.scopeCurrent().hashCode()} job3.a get from threadcontext:${SHRequestContext2.get("name")}")
                            throw  Exception("${SHRequestContext2.scopeCurrent().hashCode()} job3.a had error")
                        }
                        launch {
                            for (id in 0..100000000) {
                                if (id % 100000 == 0) {
                                    if (isActive) log("${SHRequestContext2.scopeCurrent().hashCode()} job3.b:$id")
                                }
                            }
                        }
                    }

                    delay(1000)

                    log("${SHRequestContext2.scopeCurrent().hashCode()} job0 after delay get from threadcontext:${SHRequestContext2.get("name")}")

                }
            }
        }
    }


    Thread.sleep(20000)
    log("all-end")
}

class SHRequestContext2(
        initMap: MutableMap<String, String?> = mutableMapOf()
) : ThreadContextElement<MutableMap<String, String?>>, AbstractCoroutineContextElement(ScopeKey) {
    private var _ctxMap: MutableMap<String, String?> = mutableMapOf()//copy this value to threads if thread switched
    private var threadLocalContext = ThreadLocal<MutableMap<String, String?>>()

    //Util funtions to access context map
    companion object ScopeKey : CoroutineContext.Key<SHRequestContext2> {
        suspend fun scopeCurrent(paramsMap: MutableMap<String, String?> = mutableMapOf()): SHRequestContext2 {
            var sscope = coroutineContext[SHRequestContext2]?.let { it }
                    ?: throw Exception("No [${SHRequestContext2.javaClass}] being init in parent coroutine scope! call[ SHRequestContext2.scopeInit() ]first")
            return sscope.also {
                sscope._ctxMap.putAll(paramsMap)
                sscope.threadLocalContext().putAll(paramsMap)
            }
        }

        suspend fun get(key: String): String? {
            return coroutineContext[SHRequestContext2]?.let { it.threadLocalContext()[key] }
                    ?: throw Exception("No scope being init!")
        }

        suspend fun put(key: String, value: String?) {
            coroutineContext[SHRequestContext2]?.let {
                it._ctxMap[key] = value
                it.threadLocalContext()[key] = value
            } ?: throw Exception("No scope being init!")

        }
    }

    //Quite continuation
    override fun restoreThreadContext(context: CoroutineContext, oldState: MutableMap<String, String?>) {
        oldState.clear()
    }

    //Enter continuation (possible thread switch)
    override fun updateThreadContext(context: CoroutineContext): MutableMap<String, String?> {
        return threadLocalContext().also { it.putAll(context[SHRequestContext2]!!._ctxMap) }
    }

    private fun threadLocalContext(): MutableMap<String, String?> {
        return threadLocalContext.getOrSet { mutableMapOf() }
    }

    private fun clear() {
        threadLocalContext().clear()
    }

    init {
        _ctxMap = initMap
        threadLocalContext().putAll(initMap)
    }
}
