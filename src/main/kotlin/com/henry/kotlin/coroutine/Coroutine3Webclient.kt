package com.henry.kotlin.coroutine

import kotlinx.coroutines.*
import org.springframework.web.reactive.function.client.WebClient

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * 两个协程中发生异常时，相互影响。线程池，有线程切换。
 */
fun main() {
    log("1")

    var webclient = WebClient.create()
    runBlocking {

        var p = CoroutineScope(Dispatchers.IO).async {
            var a = async {
                for (id in 0..2) {
                    if (id > 0) {
                        throw Exception("a:has error")
                    }
                    if (isActive) log("a:$id start")
                    var resp1 = webclient.get().uri("https://www.baidu.com", String::class.java).exchange()
                    resp1.subscribe()
                    if (isActive) log("a:$id ok")
                }
            }
            var b = async {
                for (id in 0..2) {
                    if (isActive) log("b:$id start")
                    var resp2 = webclient.get().uri("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.sync/-mutex/index.html", String::class.java)
                            .exchange()
                    resp2.subscribe()
                    if (isActive) log("b:$id ok")
                }
            }
        }

        p.await()

    }
}