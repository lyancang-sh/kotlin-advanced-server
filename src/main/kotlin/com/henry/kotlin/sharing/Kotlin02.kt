package com.henry.kotlin.sharing

import kotlinx.coroutines.*

/*
Coroutines Basics: 协程到底是什么?
Kotlin中用协程来做异步和非阻塞任务, 主要优点是代码可读性好, 不用回调函数. (用协程写的异步代码乍一看很像同步代码.)
:: 子协程报了异常，会自动取消父协程和其他子协程 <对比 java>
 */
fun main() {

    GlobalScope.async {

        var job1 = async {
            delay(50)
            log("line 13")
        }

        var job2 = async {
            throw Exception("oops")
            log("line 17")
        }

        delay(100)
        log("line 22")
    }

    log("line 15")
    Thread.sleep(200)
}