package com.henry.kotlin.sharing

import kotlinx.coroutines.*

/*
Coroutines Basics: CoroutineScope
关于scope:
1.我们创建一个协程的时候, 总是在一个CoroutineScope里.
2. Scope用来管理不同协程之间的父子关系和结构.

GlobalScope启动的协程没有parent, 和它被启动时所在的外部的scope没有关系. 使用GlobalScope启动两个协程也是相互独立的。
Using async or launch on the instance of GlobalScope is highly discouraged.
官方不鼓励这样使用!
 */
fun main() = runBlocking<Unit> {
    log("line 13")

    //Not creating new coroutine (superviseScope, no auto canceling on failure)
    GlobalScope.launch(Dispatchers.IO) {
        async {
            throw Exception("oops")
            log("line 25")
        }
        async {
            log("line 27")
        }
        delay(100)
        log("line 30")
    }

    GlobalScope.launch(Dispatchers.IO) {
        async {
            log("line 25_")
        }
        async {
            log("line 27_")
        }
        delay(100)
        log("line 30_")
    }
    delay(200)
    log("line 28")
}