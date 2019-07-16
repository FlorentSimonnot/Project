package com.example.validator

class validateInputPassword : ValidateField {
    var password : String = ""
    var confirmPassword : String = ""
    var size : Int = 0

    constructor(password : String, confirmPassword : String, size : Int){
        this.password = password
        this.confirmPassword = confirmPassword
        this.size = size
    }

    override fun sizeIsValid() : Boolean{
        return password.length <= size
    }

    override fun isNotEmpty(): Boolean {
        return password.isNotEmpty()
    }

    override fun fieldIsValid(): Boolean {
        return sizeIsValid() && isNotEmpty() && password == confirmPassword
    }
}