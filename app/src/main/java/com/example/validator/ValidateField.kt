package com.example.validator

interface ValidateField {

    fun sizeIsValid() : Boolean
    fun isNotEmpty() : Boolean
    fun fieldIsValid() : Boolean
}