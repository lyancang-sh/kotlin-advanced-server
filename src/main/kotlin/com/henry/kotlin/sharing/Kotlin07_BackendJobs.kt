package com.henry.kotlin.sharing

import kotlinx.coroutines.*
import org.springframework.web.client.RestTemplate

/*
Coroutine: 坑?
注意launch的用法
 */
fun main() {

    runBlocking<Unit> {

        CoroutineScope(Dispatchers.IO).launch {
            launch {
                delay(100)
                log("job1 done")
            }
            launch {
                delay(50)
                throw Exception("oops in job2...")
                log("job2 done")
            }
            launch {
                delay(100)
                log("job3 done")
            }
            launch {
                delay(100)
                log("job4 done")
            }

        }

        delay(1000)
    }
}