package com.henry.kotlin.coroutine

import kotlinx.coroutines.*

private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * step1: registering or run
 * step2: run coroutines
 *  2.1: registering or run code inside coroutines
 *  2.2: run child coroutines
 *
 * Runblocking, CoroutineDispatcher=BlockingEventLoop.queue(Continuation)
 * 同一个coroutineScope instance下面的所有job才受cancel相互影响。
 * 例如: 下面task1,2,3默认继承当前的coroutineScope(IO),所以task1出异常被cancel会导致task1的children,以及parent cancel. parent被
 * cancel会导致他的children task2,3被cancel，以此类推。
 * runBlocking(){
 *   job1 = CoroutineScope(Dispatchers.IO).async{...}
 *
 *   job2 = CoroutineScope(Dispatchers.IO).async{...}
 *
 *   job3 = CoroutineScope(Dispatchers.IO).async{
 *      async{ task1}
 *      async{ task2}
 *      async{ task3}
 *   }
 * }
 * 但是job1,job2,job3之间互不干涉，只要时间足够，job1,job2会自动继续执行直至完成。
 *
 */
//jvm args: -Dkotlinx.coroutines.debug
fun main() {
    log("all start")

    runBlocking() {

        CoroutineScope(Dispatchers.IO).async {
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                throw Exception("job1.root failed")
            }

            val job2 = CoroutineScope(Dispatchers.IO).launch {
                //Continuation 1
                for (id in 0..1000000000) {
                    if (id % 1000000000 == 0) {
                        if (isActive) log("job2.root:$id")
                    }
                }
            }

            val job3 = CoroutineScope(Dispatchers.IO).launch {
                //Continuation 1
                launch {
                    for (id in 0..1000000) {
                        if (id % 1000000 == 0) {
                            if (isActive) log("job3.a:$id")
                        }
                    }
                    throw  Exception("job3.a had error")
                }
                launch {
                    for (id in 0..100000000) {
                        if (id % 100000 == 0) {
                            if (isActive) log("job3.b:$id")
                        }
                    }
                }
            }


        }
    }
    Thread.sleep(5000)
    log("all-end")
}