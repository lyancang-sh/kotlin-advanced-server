package com.henry.kotlin.sharing

import kotlinx.coroutines.*

/*
Coroutines Basics: 创建协程的方式
常用方式(coroutine builders):
runBlocking:建立一个阻塞当前线程的协程(Coroutine),等待所有子的携程完成才结束（前提是同一个scope)。
launch:返回Job,使用job.join()显示等待结束。也可以cancel().
async:返回Deferred,使用deffered.await()等待结果

 */
fun main() = runBlocking<Unit> {
    log("line 13")

    GlobalScope.async {//GlobalScope Like daemon threads,not blocking process
        log("line 16")
        delay(200)
        log("line 18")
    }

    GlobalScope.launch {
        log("line 22")
        delay(100)
        log("line 24")
    }

    async {
        log("line 28")
        delay(300)
        log("line 3000")
    }

    log("line 33")
}