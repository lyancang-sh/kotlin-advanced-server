package com.henry.kotlinadvanced

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private fun mymethod2(): Flow<Int> = flow {
  for(i in 1..4){
      delay(100)
      emit(i)
  }
}
fun main() = runBlocking<Unit> {
    launch {
        for(i in 1..4){
            delay(100)
            println("ok,$i")
        }
    }
    mymethod2().collect { println(it) }
}