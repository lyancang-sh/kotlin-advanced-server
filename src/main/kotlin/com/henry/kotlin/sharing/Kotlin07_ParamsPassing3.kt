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
    runBlocking<Unit>(Dispatchers.IO + MyContextMap(mutableMapOf("name" to "zhangsan"))) {
        var con = coroutineContext[MyContextMap.scopeKey]

        async {
            coroutineContext[MyContextMap.scopeKey]?.myMap?.put("name","lisi")
            log("from context:" + coroutineContext[MyContextMap.scopeKey]?.myMap?.get("name"))
        }

        CoroutineScope(Dispatchers.IO+ con!!).launch {
            log("line 21:" + coroutineContext[MyContextMap.scopeKey]?.myMap?.get("name"))
        }


        log("line 24:" + coroutineContext[MyContextMap.scopeKey]?.myMap?.get("name"))
        delay(1000)
    }
}

class MyContextMap(var myMap:MutableMap<String,String>?):ThreadContextElement<MyContextMap>{
    override val key: CoroutineContext.Key<*>
        get() = scopeKey
    companion object scopeKey : CoroutineContext.Key<MyContextMap>

    override fun restoreThreadContext(context: CoroutineContext, oldState: MyContextMap) {

    }

    override fun updateThreadContext(context: CoroutineContext): MyContextMap {
        context[scopeKey]?.myMap?.let { myMap?.putAll(it) }
        return this
    }


}
