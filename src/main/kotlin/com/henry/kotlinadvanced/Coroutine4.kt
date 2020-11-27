package com.henry.kotlinadvanced

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private suspend fun mymethod():List<String>{
    delay(1000)
    return listOf("hello","world","fine")
}
fun main() = runBlocking {
    mymethod().forEach { println(it) }
}