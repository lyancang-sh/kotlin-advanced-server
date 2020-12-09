package com.henry.kotlin.sharing

import kotlinx.coroutines.*

/*
Coroutines Basics: CoroutineScope
关于scope:
1.我们创建一个协程的时候, 总是在一个CoroutineScope里.
2. Scope用来管理不同协程之间的父子关系和结构.

CoroutineScope达到类似相互隔离效果。
官方推荐!
 */
fun main() = runBlocking<Unit> {
    log("line 13")

    //Not creating new coroutine (superviseScope, no auto canceling on failure)
    CoroutineScope(Dispatchers.IO).launch {
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

    CoroutineScope(Dispatchers.IO).launch {
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