package com.example.validator

class validateInputInt(
    val number : Int,
    val min : Int,
    val max : Int
) {

    fun fieldIsValid() : Boolean{
        return number in min until max
    }
}