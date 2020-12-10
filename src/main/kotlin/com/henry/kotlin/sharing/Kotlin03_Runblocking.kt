package com.henry.kotlin.sharing

import kotlinx.coroutines.*

/*
Coroutines Basics: 创建协程的方式
常用方式(coroutine builders):
runBlocking:建立一个阻塞当前线程的协程(Coroutine),等待所有子的携程完成才结束（前提是同一个scope)。
launch:返回Job,使用job.join()显示等待结束。也可以cancel().
async:返回Deferred,使用deffered.await()等待结果

 */
fun main() = runBlocking { // this: CoroutineScope
    launch {
        delay(200L)
        println("Task from runBlocking")
    }

    coroutineScope { // Creates a coroutine scope
        launch {
            delay(500L)
            println("Task from nested launch")
        }

        delay(100L)
        println("Task from coroutine scope") // This line will be printed before the nested launch
    }

    println("Coroutine scope is over") // This line is not printed until the nested launch completes
}