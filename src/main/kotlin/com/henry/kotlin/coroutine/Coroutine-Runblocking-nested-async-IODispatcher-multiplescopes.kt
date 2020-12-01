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
    log("all start")

    runBlocking() {

        CoroutineScope(Dispatchers.IO).async {
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                //Continuation 1
//                for (id in 0..100000) {
//                    if (id % 100000 == 0) {
//                        if (isActive) log("1.a:$id")
//                    }
//                }
                throw Exception("1.async 1.a1 failed")
            }

            val job2 = CoroutineScope(Dispatchers.IO).launch {
                //Continuation 1
                for (id in 0..10000000) {
                    if (id % 10000000 == 0) {
                        if (isActive) log("2.a:$id")
                    }
                }
            }

            val job3 = CoroutineScope(Dispatchers.IO).launch {
                //Continuation 1
                for (id in 0..10000000) {
                    if (id % 10000000 == 0) {
                        if (isActive) log("3.a:$id")
                    }
                }
            }

            for (ie in 0..100) {
                val t = CoroutineScope(Dispatchers.IO).launch {
                    //Continuation 1
                    for (id in 0..10000000) {
                        if (id % 10000000 == 0) {
                            if (isActive) log("t$ie.a:$id")
                        }
                    }
                    println("ie done!!!!!!! $ie")
                }
            }

        }
    }
    Thread.sleep(5000)
    log("all-end")
}