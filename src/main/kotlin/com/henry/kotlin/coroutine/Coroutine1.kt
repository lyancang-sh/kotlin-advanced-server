package com.henry.kotlin.coroutine

import kotlinx.coroutines.*

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * 两个协程中发生异常时，相互影响。线程池，有线程切换。
 */
fun main() {
    log("1")

    runBlocking {


        var p = CoroutineScope(Dispatchers.IO).async {
            var a = async {
                for (id in 0..1000000) {
                    if (id % 10000 == 0) {
                        if (isActive) log("a:$id")
                    }
                    if (id > 500000 && Math.random() < 0.0001) {
                        throw Exception("a:has error")
                    }
                }
            }
            var b = async {
                for (id in 0..1000000) {
                    if (id % 10000 == 0) {
                        if (isActive) log("b:$id")
                    }
                }
            }
        }

        p.await()

    }
}