package com.henry.kotlin.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive

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

    runBlocking {

        var a = suspend_a()

        var b = suspend_b()

        log("run blocking last line")

    }
    log("1-end")
}

suspend fun suspend_a() = withContext(currentCoroutineContext()) {
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

suspend fun suspend_b() = withContext(currentCoroutineContext()) {
//Continuation 1
    for (id in 0..1000000) {
        if (id % 1000000 == 0) {
            if (isActive) log("b:$id")
        }
    }
}

suspend fun suspend_c() = withContext(currentCoroutineContext()) {
//Continuation 1
    for (id in 0..1000000) {
        if (id % 1000000 == 0) {
            if (isActive) log("c:$id")
        }
    }
}