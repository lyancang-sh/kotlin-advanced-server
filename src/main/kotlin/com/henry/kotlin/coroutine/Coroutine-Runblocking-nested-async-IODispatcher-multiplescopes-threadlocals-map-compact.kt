package com.henry.kotlin.coroutine

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.getOrSet
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

    runBlocking(SHRequestContext.scopeInit(mutableMapOf("name" to "someone else...."))) {

        CoroutineScope(Dispatchers.IO + SHRequestContext.scopeCurrent()).async {
            log("job0 before delay get from threadcontext:${SHRequestContext.get("name")}")
            val job1 = CoroutineScope(Dispatchers.IO + SHRequestContext.scopeCurrent()).launch {
                log("job1 get from threadcontext:${SHRequestContext.get("name")}")
                SHRequestContext.put("name", "henryliang")
                throw Exception("job1.root failed")
            }

            val job2 = CoroutineScope(Dispatchers.IO + SHRequestContext.scopeCurrent(mutableMapOf("age" to "88"))).launch {
                log("job2 get from threadcontext age=:${SHRequestContext.get("age")}")
                //Continuation 1
                for (id in 0..1000000000) {
                    if (id % 1000000000 == 0) {
                        if (isActive) log("job2.root:$id")
                    }
                }
            }

            //NOTE: Didn't pass as context, so nothing will be there
            val job3 = CoroutineScope(Dispatchers.IO + SHRequestContext.scopeCurrent()).launch {
                SHRequestContext.put("gender", "male")
                log("job3 get from threadcontext:${SHRequestContext.get("name")}")
                log("job3 get from threadcontext:gender=${SHRequestContext.get("gender")}")
                //Continuation 1
                launch {
                    for (id in 0..1000000) {
                        if (id % 1000000 == 0) {
                            if (isActive) log("job3.a:$id")
                        }
                    }
                    log("job3.a get from threadcontext:${SHRequestContext.get("name")}")
                    throw  Exception("job3.a had error")
                }
                launch {
                    for (id in 0..100000000) {
                        if (id % 100000 == 0) {
                            if (isActive) log("job3.b:$id")
                        }
                    }
                }
            }

            delay(1000)

            log("job0 after delay get from threadcontext:${SHRequestContext.get("name")}")

        }
    }
    Thread.sleep(5000)
    log("all-end")
}

class SHRequestContext(override val key: CoroutineContext.Key<*> = ScopeKey
) : ThreadContextElement<ConcurrentHashMap<String, String?>> {
    private var _ctxMap: MutableMap<String, String?> = mutableMapOf()//copy this value to threads if thread switched

    companion object ScopeKey : CoroutineContext.Key<SHRequestContext> {
        fun scopeInit(paramsMap: MutableMap<String, String?> = mutableMapOf()): SHRequestContext {
            var sscope = SHRequestContext()
            sscope._ctxMap = paramsMap
            return sscope
        }

        suspend fun scopeCurrent(paramsMap: MutableMap<String, String?> = mutableMapOf()): SHRequestContext {
            var sscope = coroutineContext[SHRequestContext]?.let { it }
                    ?: throw Exception("No [${SHRequestContext.javaClass}] being init in parent coroutine scope! call[ SHRequestContext.scopeInit() ]first")
            sscope._ctxMap.putAll(paramsMap)
            return sscope
        }

        suspend fun get(key: String): String? {
            var sscope = coroutineContext[SHRequestContext]?.let { it } ?: throw Exception("No scope being init!")
            return sscope.threadLocalContext()[key]
        }

        suspend fun put(key: String, value: String?) {
            var sscope = coroutineContext[SHRequestContext]?.let { it } ?: throw Exception("No scope being init!")
            sscope._ctxMap[key] = value
            sscope.threadLocalContext()[key] = value
        }
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: ConcurrentHashMap<String, String?>) {
        oldState.clear()
    }

    //affects continuation.continue
    override fun updateThreadContext(context: CoroutineContext): ConcurrentHashMap<String, String?> {
        threadLocalContext().putAll(_ctxMap)
        return threadLocalContext()
    }

    var threadLocalContext = ThreadLocal<ConcurrentHashMap<String, String?>>()

    private fun threadLocalContext(): ConcurrentHashMap<String, String?> {
        return threadLocalContext.getOrSet { ConcurrentHashMap<String, String?>() }
    }

    private fun clear() {
        threadLocalContext().clear()
    }
}
