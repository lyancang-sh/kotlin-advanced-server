package com.henry.kotlin.sharing

import kotlinx.coroutines.*
import org.springframework.web.client.RestTemplate

/*
Coroutine: 坑?
注意asyncd的异常catch点，出异常马上返回还是出异常等所有coroutine完成再返回
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
            log("result: ${child1!!.await()} , ${child2!!.await()}")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        Thread.sleep(10000)
    }
}