package com.henry.kotlin.sharing

import kotlinx.coroutines.*

/*
Coroutines Basics: CoroutineScope
关于scope:
1.我们创建一个协程的时候, 总是在一个CoroutineScope里.
2. Scope用来管理不同协程之间的父子关系和结构.

Scope.job -> children.jobs)
Scope.context->share context with children coroutines

可以手动创建CoroutineScope
MainScope() //UI Thread
CoroutineScope(Dispatchers.IO) // Specify Tread(s),产生新的coroutine,
coroutineScope {} //同一个coroutine,不会切换
scope的主要作用就是记录所有的协程, 并且可以取消它们. 这种利用scope将协程结构化组织起来的机制, 被称为"structured concurrency".
***
scope自动负责子协程, 子协程的生命和scope绑定.
scope可以自动取消所有的子协程.
scope自动等待所有的子协程结束. 如果scope和一个parent协程绑定, 父协程会等待这个scope中所有的子协程完成.

下面例子中: coroutineScope和runBlocking的scope是同一个,里面的出了异常，则runBlocking中的所有coroutines都会被cancel()
withContext(coroutineContext) run code in current scope
 */
fun main() = runBlocking<Unit> {
    log("line 13")
    //Not creating new coroutine (superviseScope, no auto canceling on failure)
//    withContext(coroutineContext){//No coroutine will be created
    coroutineScope {
        withContext(coroutineContext) {
            async {
                throw Exception("oops")
                log("line 25")
            }
            async {
                log("line 27")
            }
        }
        delay(100)
        log("line 30")
    }
    delay(200)
    log("line 28")
}