package com.henry.kotlin.coroutine

import kotlinx.coroutines.*

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * step1: registering or run
 * step2: run coroutines
 * Runblocking, CoroutineDispatcher=BlockingEventLoop.queue(Continuation) for this case, 2 continuations in queue
 *
 */
//jvm args: -Dkotlinx.coroutines.debug
fun main() {
    log("1")

    runBlocking {

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