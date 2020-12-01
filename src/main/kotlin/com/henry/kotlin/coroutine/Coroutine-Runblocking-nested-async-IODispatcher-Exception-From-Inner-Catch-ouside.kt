package com.henry.kotlin.coroutine

import kotlinx.coroutines.*

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * step1: registering or run
 * step2: run coroutines
 *  2.1: registering or run code inside coroutines
 *  2.2: run child coroutines
 *
 *  runBlocking.coroutine-a run half sync
 *  runBlocking.coroutine-b
 *  runBlocking.coroutine-c
 *  --
 *  runBlocking.coroutine-b.subcoroutine-1 run half sync
 *  runBlocking.coroutine-b.subcoroutine-2
 *  runBlocking.coroutine-b.subcoroutine-3
 *
 * Dispatchers.Default, when b.1 had exception, it will cancel children b.2/b.3 + parent.cancel()
 * in below example: c will be stopped arbituarily
 * Runblocking, CoroutineDispatcher=BlockingEventLoop.queue(Continuation)
 *
 * Note: 协程的取消是可传播的，会cancel所有的parent.
 *
 * 下面这样的代码在协程外部，没有意义，抓不住协程内部的exception,不管是不是自定义异常类。
 * 1.放到协程代码内部直接抓取。比如在throw exception的方法上面加。
 * 2.在协程上await等待结果，结果可能是异常，这样的话放在协程外部的catch才能抓住。
 */
//jvm args: -Dkotlinx.coroutines.debug
fun main() {
    log("1")

    runBlocking(Dispatchers.Default) {
        try {
            //@0
            log("run blocking first line")

            var a = async {
                //Continuation 1
                for (id in 0..1000000) {
                    if (id % 1000000 == 0) {
                        if (isActive) log("a:$id")
                    }
                }
                delay(100)
                //Continuation 2
                log("a:after delay")
            }

            var b = async {
                //Continuation 3
                for (id in 0..1000000) {
                    if (id % 1000000 == 0) {
                        if (isActive) log("b:$id")
                    }
                }
                //这样的代码在协程外部，没有意义，抓不住协程内部的exception,不管是不是自定义异常类。
                //需要放到协程代码内部才行。比如在throw exception的方法上面加。
//            try { //@1
                launch {
                    //Continuation 5
//                    try { //@2
                    for (id in 0..1000000) {
                        if (id % 1000000 == 0) {
                            if (isActive) log("b.1:$id")
                            if (Math.random() < 0.5) {
                                throw CustomEx("throw error from b.1")
                            }
                        }
                    }
//                } catch (ex: CustomEx) {
//                    log("had error inside inner try-catch:${ex.toString()}")
//                }
                    "b"
                }
//            } catch (ex: CustomEx) {
//                log("had error inside inner try-catch:${ex.toString()}")
//            }
                async {
                    //Continuation 5
                    for (id in 0..1000000) {
                        if (id % 1000000 == 0) {
                            if (isActive) log("b.2:$id")
                        }
                    }
                }
                log("b.* done")
            }

            log("run blocking 1")

            var c = async {
                //Continuation 4
                for (id in 0..1000000) {
                    if (id % 1000 == 0) {
                        if (isActive) log("c:$id")
                    }
                }
            }

            log("run blocking last line")
            println(b.await())//关键,必须await才能在此获得exception
        } catch (ex: CustomEx) {
            log("Caught:had error inside inner try-catch:${ex.toString()}")
        }
    }
    log("1-end")
}