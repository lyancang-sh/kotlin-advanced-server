package com.henry.kotlin.coroutine

import kotlinx.coroutines.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*


/**
 * Parent Coroutine Scope.await(): if any of sub-coroutine throws exception, parent will wait for second to run complete, then throws first exceptions
 * If missing Parent Coroutine Scope.await(), instead, wait on sub_Coroutine_1.await(), then exception will be thrown out first, and then, if sub_coroutine_2 is started, it will resume util tone
 *
 * parent.await()先把所有的子协程cancel再返回exception,否则，直接返回第一个被await()的协程中的exception
 *
 */
fun main() {

    runBlocking {
        var child1: Deferred<Unit>? = null
        var child2: Deferred<Unit>? = null
        try {

            var parent = CoroutineScope(Dispatchers.IO).async {

                child1 = async {
                    println("async1 started")
                    for (int in 0..1000000) {
                        if (int % 1000000 == 0) println(Thread.currentThread().name + "async1 $int")
                    }
                    throw Exception("async1 failed")
                    println("async1 ended")
                }

//                child1!!.await()

                child2 = async {
                    println("async2 started")
                    for (int in 0..10000000) {
                        if (int % 1000000 == 0) println(Thread.currentThread().name + "async2 $int")
                    }
//                    throw Exception("async2 failed")
                    println("async2 ended")
                }
            }
            print("result: ${parent.await()}")//Flip 1
//            print("result: ${child1!!.await()},${child2!!.await()}") //Flip2
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        Thread.sleep(10000)
    }
}