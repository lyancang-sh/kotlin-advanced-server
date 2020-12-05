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
    runBlocking(Dispatchers.IO) {
        log("start")
        for (id in 0..5) {
            delay(1000)
            log("run after delay: $id")
        }

        log("end")
    }
}

