package com.henry.kotlin.coroutine

import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * 两个协程中发生异常时，相互影响。newSingleThreadContext,同步阻塞运行，没有线程切换
 */
fun main() {
    log("1")

    runBlocking {


        var p = newSingleThreadContext("single").use {
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


    }
}