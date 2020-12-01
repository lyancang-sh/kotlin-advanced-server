package com.henry.kotlin.coroutine

import kotlinx.coroutines.*

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * step1: registering or run
 * step2: run coroutines
 *  2.1: registering or run code inside coroutines
 *  2.2: run child coroutines
 *
 *  runBlocking.coroutine-a run half sync
 *  runBlocking.coroutine-b
 *  runBlocking.coroutine-c
 *  --
 *  runBlocking.coroutine-b.subcoroutine-1 run half sync
 *  runBlocking.coroutine-b.subcoroutine-2
 *  runBlocking.coroutine-b.subcoroutine-3
 *
 * Dispatchers.Default, when b.1 had exception, it will cancel children b.2/b.3 + parent.cancel()
 * in below example: c will be stopped arbituarily
 * Runblocking, CoroutineDispatcher=BlockingEventLoop.queue(Continuation)
 *
 * Note: 协程的取消是可传播的，会cancel所有的parent.
 *
 */
//jvm args: -Dkotlinx.coroutines.debug
fun main() {
    log("1")

    runBlocking(Dispatchers.Default) {
        log("run blocking first line")
        var a = async {
            //Continuation 1
            for (id in 0..1000000) {
                if (id % 1000000 == 0) {
                    if (isActive) log("a:$id")
                }
            }
            delay(100)
            //Continuation 2
            log("a:after delay")
        }

        var b = async {
            //Continuation 3
            for (id in 0..1000000) {
                if (id % 1000000 == 0) {
                    if (isActive) log("b:$id")
                }
            }

            launch {
                //Continuation 5
                for (id in 0..1000000) {
                    if (id % 1000000 == 0) {
                        if (isActive) log("b.1:$id")
                        if (Math.random() < 0.5) {
                            throw Exception("throw error from b.1")
                        }
                    }
                }
            }
            async {
                //Continuation 5
                for (id in 0..1000000) {
                    if (id % 1000000 == 0) {
                        if (isActive) log("b.2:$id")
                    }
                }
            }
            log("b.* done")
        }

        log("run blocking 1")

        var c = async {
            //Continuation 4
            for (id in 0..1000000) {
                if (id % 100 == 0) {
                    if (isActive) log("c:$id")
                }
            }
        }

        log("run blocking last line")

    }
    log("1-end")
}