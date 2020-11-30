package com.henry.kotlin.coroutine

import kotlinx.coroutines.*

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * step1: registering or run
 * step2: run coroutines
 *  2.1: registering or run code inside coroutines
 *  2.2: run child coroutines
 *
 * Runblocking, CoroutineDispatcher=BlockingEventLoop.queue(Continuation)
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

            async {
                //Continuation 5
                for (id in 0..1000000) {
                    if (id % 1000000 == 0) {
                        if (isActive) log("b.1:$id")
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
                if (id % 1000000 == 0) {
                    if (isActive) log("c:$id")
                }
            }
        }

        log("run blocking last line")

    }
    log("1-end")
}