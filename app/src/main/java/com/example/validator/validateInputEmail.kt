package com.example.validator

import android.util.Patterns

class validateInputEmail  : ValidateField{
    private var email : String = ""
    private var size : Int = 0

    constructor(email : String, size : Int){
        this.email = email
        this.size = size
    }

    override fun sizeIsValid() : Boolean{
        return email.length <= size
    }

    override fun isNotEmpty(): Boolean {
        return email.isNotEmpty()
    }

    private fun isValidEmail() : Boolean{
        println(" IsValidEmail    :::::: " + Patterns.EMAIL_ADDRESS.toRegex().matches(email))
        return Patterns.EMAIL_ADDRESS.toRegex().matches(email)
    }

    override fun fieldIsValid(): Boolean {
        return sizeIsValid() && isNotEmpty() && isValidEmail()
    }

}