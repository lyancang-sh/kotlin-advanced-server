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
    log(test_x1())
    log(test_x2())
    log("1-end")
}


fun test_x1(): String = runBlocking {
    return@runBlocking "111"
}

fun test_x2(): String = runBlocking {
    "222"
}