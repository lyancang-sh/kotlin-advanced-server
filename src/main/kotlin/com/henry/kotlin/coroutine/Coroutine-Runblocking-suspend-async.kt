package com.henry.kotlin.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * coroutine is created by keyword (async,launch)
 *
 * suspend is just tell caller that this is a time-consuming api, and is requried/recommended to use coroutines to call
 */
//jvm args: -Dkotlinx.coroutines.debug
fun main() {
    log("1")

    runBlocking {

        var a = async { suspend_a() }

        var b = async { suspend_b() }

        var c = launch { suspend_c() }

        log("run blocking last line")

    }
    log("1-end")
}