package com.henry.kotlin.sharing

import kotlinx.coroutines.*

/*
Coroutines Basics: 协程到底是什么?
Kotlin中用协程来做异步和非阻塞任务, 主要优点是代码可读性好, 不用回调函数. (用协程写的异步代码乍一看很像同步代码.)
对比与java中线程
 */
fun main() {

    Thread() {
        log("line 12")
    }.start()

    log("line 15")
    Thread.sleep(200)
}