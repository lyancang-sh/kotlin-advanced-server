package com.henry.kotlinadvanced

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class Man {

    companion object ABC : Person("testing") {

    }
}

open class Person(var name: String)

fun main() {
    println(Man.ABC.name)
}