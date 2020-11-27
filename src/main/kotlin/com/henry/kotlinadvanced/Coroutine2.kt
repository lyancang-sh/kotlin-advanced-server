package com.henry.kotlinadvanced

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/*
Cancel parent coroutinescope, all children coroutine will be terminated
 */
private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    fun destroy() {
        cancel()
    }

    fun doSth() {
        repeat(8) { i ->
            launch {
                delay((i + 1) * 300L)//base is 0,not based on previous co-rutine
                println("coroutine is finished: $i")
            }
        }
    }
}

fun main() = runBlocking {
    var ac = Activity()
    ac.doSth()

    println("--------------")
    delay(1300)

    println("ending")
    ac.cancel()
    delay(5000)
}