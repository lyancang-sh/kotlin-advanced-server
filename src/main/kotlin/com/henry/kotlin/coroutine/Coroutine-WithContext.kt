package com.henry.kotlin.coroutine

import kotlinx.coroutines.*
import java.util.HashMap

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 *
 * Coroutine-> * Continuations(if meet suspending fuctions)
 */
fun main() {
    log("line 11")

    runBlocking {
        log("line 14")
        withContext(coroutineContext) {
            log("line 16")
            async {
                delay(1000)
                log("do something in line 18 ")
            }
//            for (id in 0..1000000) {
//                if (id % 1000000 == 0) {
//                    if (isActive) log("b:$id")
//                }
//            }
            async {
                log("do something in line 28 ")
            }
            log("with context end")
        }
    }

    Thread.sleep(10000)
}

suspend fun doSth(i: String) {
    delay(100)
    log(i)
}