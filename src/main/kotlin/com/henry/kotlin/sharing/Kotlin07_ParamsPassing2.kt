package com.henry.kotlin.sharing

import kotlinx.coroutines.*
import kotlin.concurrent.getOrSet
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/*
Coroutine: 坑?
如何将变量在协程和其线程之间切换?借助ThreadLocal和ContextElement
 */
fun main() {
    runBlocking<Unit>(Dispatchers.IO + ContextParam("zhangsan")) {
        var con = coroutineContext[ContextParam.scopeKey]

        async {
            coroutineContext[ContextParam.scopeKey]?.name="wangwu"
            log("from context:" + coroutineContext[ContextParam.scopeKey]?.name)
        }

        CoroutineScope(Dispatchers.IO+ con!!).launch {
            log("line 21:" + coroutineContext[ContextParam.scopeKey]?.name)
        }


        log("line 24:" + coroutineContext[ContextParam.scopeKey]?.name)
        delay(1000)
    }
}

class ContextParam(var name:String?):ThreadContextElement<ContextParam>{
    override val key: CoroutineContext.Key<*>
        get() = scopeKey
    companion object scopeKey : CoroutineContext.Key<ContextParam>

    override fun restoreThreadContext(context: CoroutineContext, oldState: ContextParam) {

    }

    override fun updateThreadContext(context: CoroutineContext): ContextParam {
        name = context[scopeKey]?.name
        return this
    }


}
