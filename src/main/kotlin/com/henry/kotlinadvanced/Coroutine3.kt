package com.henry.kotlinadvanced

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/*
Threadlocal
 */
val localVar = ThreadLocal<String>()

fun main()= runBlocking {
    localVar.set("hello")
    println("thread:${Thread.currentThread().name},val=${localVar.get()}")
    
    var job=launch (Dispatchers.Default+ localVar.asContextElement(value = "world") ){
        println("thread:${Thread.currentThread().name},val=${localVar.get()}")
        yield() //change thread
        localVar.set("hahahahah")

        println("thread:${Thread.currentThread().name},val=${localVar.get()}")
    }
    job.join()
    println("thread:${Thread.currentThread().name},val=${localVar.get()}")
}
