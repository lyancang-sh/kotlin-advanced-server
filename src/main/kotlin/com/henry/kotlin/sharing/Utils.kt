package com.henry.kotlin.sharing


fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")