package com.henry.kotlinadvanced

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

private fun mymethod2(): Flow<Int> = flow {
  for(i in 1..4){
      delay(100)
      emit(i)
  }
}.flowOn(Dispatchers.Default)
fun main() = runBlocking<Unit> {
    mymethod2().onCompletion { t->if(t!=null){
        println("had error")}}.collect { println("ok,$it") }
}