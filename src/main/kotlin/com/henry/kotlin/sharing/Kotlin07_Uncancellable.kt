package com.henry.kotlin.sharing

import kotlinx.coroutines.*
import org.springframework.web.client.RestTemplate

/*
Coroutine: 坑?
不太好重现
 */
fun main() {

    runBlocking<Unit> {
        var child1: Deferred<Unit>? = null
        var child2: Deferred<Unit>? = null

        try {
            var parent = CoroutineScope(Dispatchers.IO).async {
                child1 = async {
                    delay(100)
                    log("async1 started")
                    for (int in 0..100000) {
                        if (int % 100000 == 0) log(Thread.currentThread().name + "async1 $int")
                    }
                    throw Exception("async1 failed")
                    log("async1 ended")
                }
                child2 = async {
                    delay(90)
                    log("async2 started")
                    for (int in 0..100000000) {
                        if (int % 10000000 == 0) log(Thread.currentThread().name + "async2 $int")
                    }
                    log("async2 ended")
                }

            }
            delay(100)
//            print("result: ${parent.await()}")//Flip 1, capture child1 exception->wait for child2 done->return child1 exception
            log("result: ${child1!!.await()} , ${child2!!.await()}") //Flip2, capture child1 exception->return child1 exception->child2 run in backend till done
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        Thread.sleep(10000)
    }
}