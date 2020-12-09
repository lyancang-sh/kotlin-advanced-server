package com.henry.kotlin.sharing

import kotlinx.coroutines.yield

/*
Coroutines Basics: 协程到底是什么?
Kotlin中用协程来做异步和非阻塞任务, 主要优点是代码可读性好, 不用回调函数. (用协程写的异步代码乍一看很像同步代码.)
//Java. Method2 需要手动使用threadGroup.intercept
 */
fun main() {

    var group = ThreadGroup("group1")

    Thread(group) {
        try {
            Thread.sleep(1000)
            for (i in 0..1000000) {
                if (i % 100000 == 0)
                    log("line 18 $i")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            group.interrupt()
        }

    }.start()

    Thread(group) {
        try {
            throw Exception("oops")
            log("line 14")
        } catch (ex: Exception) {
            ex.printStackTrace()
            group.interrupt()
        }
    }.start()

    log("line 21")
    Thread.sleep(2000)
}