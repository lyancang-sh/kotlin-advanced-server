package com.henry.kotlin.sharing

import kotlinx.coroutines.*
import org.springframework.web.client.RestTemplate

/*
Coroutine: 坑?
注意 await()的必要性
 */
fun main() {
    runBlocking<Unit> {
        var parent = CoroutineScope(Dispatchers.IO).async {
            async {
                delay(100)
                log("async1 started")
                for (int in 0..100000) {
                    if (int % 100000 == 0) log(Thread.currentThread().name + "async1 $int")
                }
                throw Exception("async1 failed")
                log("async1 ended")
            }
            async {
                delay(90)
                log("async2 started")
                for (int in 0..100000000) {
                    if (int % 10000000 == 0) log(Thread.currentThread().name + "async2 $int")
                }
                log("async2 ended")
            }

        }
        log("api success")
    }
}