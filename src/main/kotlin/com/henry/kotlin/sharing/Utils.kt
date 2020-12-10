package com.henry.kotlin.sharing


fun log(msg: String) = println("${System.currentTimeMillis()} [${Thread.currentThread().name}] $msg")