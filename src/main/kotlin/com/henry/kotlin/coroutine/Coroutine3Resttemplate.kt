package com.henry.kotlin.coroutine

import kotlinx.coroutines.*
import org.springframework.web.client.RestTemplate

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * 两个协程中发生异常时，相互影响。线程池，有线程切换。
 */
fun main() {
    log("1")

    var restTemplate = RestTemplate()
    runBlocking {

        var p = CoroutineScope(Dispatchers.IO).async {
            var a = async {
                for (id in 0..2) {
                    if (id > 0) {
                        throw Exception("a:has error")
                    }
                    if (isActive)  log("a:$id start")
                    restTemplate.getForObject("https://www.baidu.com", String::class.java)
                    if (isActive) log("a:$id ok")
                }
            }
            var b = async {
                for (id in 0..2) {
                    log("b:$id start")
                    restTemplate.getForObject("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.sync/-mutex/index.html", String::class.java)
                    log("b:$id ok")
                }
            }
        }

        p.await()

    }
}