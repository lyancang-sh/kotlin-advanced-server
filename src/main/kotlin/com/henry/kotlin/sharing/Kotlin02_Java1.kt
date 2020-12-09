package com.henry.kotlin.sharing

import kotlinx.coroutines.yield

/*
Coroutines Basics: 协程到底是什么?
Kotlin中用协程来做异步和非阻塞任务, 主要优点是代码可读性好, 不用回调函数. (用协程写的异步代码乍一看很像同步代码.)
//Java. Method1 需要手动使用锁对象上的wait()/notify()等来实现线程之间通信
 */
fun main() {

    var statusLock = ThreadStatus.OK

    Thread() {
        synchronized(statusLock) {
            if (statusLock == ThreadStatus.OK) {
                try {
                    throw Exception("oops")
                    log("line 14")
                } catch (ex: Exception) {
                    statusLock = ThreadStatus.ERROR
                }
            }
        }
    }.start()

    Thread() {
        synchronized(statusLock) {
            if (statusLock == ThreadStatus.OK) {
                try {
                    log("line 18")
                } catch (ex: Exception) {
                    statusLock = ThreadStatus.ERROR
                }
            } else {
                System.currentTimeMillis()
            }
        }

    }.start()


    log("line 21")
    Thread.sleep(200)
}

enum class ThreadStatus {
    OK, ERROR
}