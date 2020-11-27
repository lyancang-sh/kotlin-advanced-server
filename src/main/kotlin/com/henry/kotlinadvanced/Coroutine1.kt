package com.henry.kotlinadvanced

import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
private fun log(msg:String) = println("[${Thread.currentThread().name}] $msg")

fun main() = runBlocking {
    val a = async {
        log("hello")
        10
    }

    val b = async {
        log("world")
        20
    }

    val c = newSingleThreadContext("sss").use {
        log("single")
        30
    }
    log("done : ${a.await()} and ${b.await()} and $c")
}