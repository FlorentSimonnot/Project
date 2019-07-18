package com.example.utils

class Utils {
    private val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    fun generatePassword(size : Int) : String{
        return (1..size)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.length) }
            .map(charPool::get)
            .joinToString("");
    }
}