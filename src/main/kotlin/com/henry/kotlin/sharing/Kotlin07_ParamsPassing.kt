package com.henry.kotlin.sharing

import kotlinx.coroutines.*
import kotlin.concurrent.getOrSet
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/*
Coroutine: 坑?
如何将变量在协程和其线程之间切换?借助ThreadLocal和ContextElement
 */
fun main() {
    runBlocking<Unit>(Dispatchers.IO + RequestContextScope(mutableMapOf("name" to "baybay"))) {

        async {
            log("from context:" + RequestContextScope.getKey("name"))
        }

        CoroutineScope(Dispatchers.IO+RequestContextScope.current()).launch {
            log("line 21:" + RequestContextScope.getKey("name"))
        }

        log("line 24:" + RequestContextScope.getKey("name"))
        delay(1000)
    }
}


class RequestContextScope(
        initMap: MutableMap<String, String?> = mutableMapOf()
) : ThreadContextElement<MutableMap<String, String?>>, AbstractCoroutineContextElement(ScopeKey) {
    private var _ctxMap: MutableMap<String, String?> = mutableMapOf()//copy this value to threads if thread switched
    private var threadLocalContext = ThreadLocal<MutableMap<String, String?>>()

    //Util funtions to access context map
    companion object ScopeKey : CoroutineContext.Key<RequestContextScope> {
        suspend fun current(paramsMap: MutableMap<String, String?> = mutableMapOf()): RequestContextScope {
            var sscope = coroutineContext[RequestContextScope]?.let { it }
                    ?: throw Exception("No [${RequestContextScope.javaClass}] being initialized in this coroutine scope!")
            return sscope.also {
                sscope._ctxMap.putAll(paramsMap)
                sscope.threadLocalContext().putAll(paramsMap)
            }
        }

        suspend fun getKey(key: String): String? {
            return coroutineContext[RequestContextScope]?.let { it.threadLocalContext()[key] }
                    ?: throw Exception("No [${RequestContextScope.javaClass}] being initialized in this coroutine scope!")
        }

        suspend fun put(key: String, value: String?) {
            coroutineContext[RequestContextScope]?.let {
                it._ctxMap[key] = value
                it.threadLocalContext()[key] = value
            } ?: throw Exception("No [${RequestContextScope.javaClass}] being initialized in this coroutine scope!")

        }
    }

    //Quite continuation
    override fun restoreThreadContext(context: CoroutineContext, oldState: MutableMap<String, String?>) {
        oldState.clear()
    }

    //Enter continuation (possible thread switch)
    override fun updateThreadContext(context: CoroutineContext): MutableMap<String, String?> {
        return threadLocalContext().also { it.putAll(context[RequestContextScope]!!._ctxMap) }
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