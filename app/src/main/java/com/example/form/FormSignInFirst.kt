package com.example.form

import com.example.validator.validateInputEmail
import com.example.validator.validateInputPassword
import com.example.validator.validateInputText

data class FormSignInFirst (
    val firstName : String = "",
    val name : String = "",
    val email : String = "",
    val password : String = "",
    val confirmPassword : String = "") : Form{

    private var size : Int = 255

    override fun isFormValid(): Boolean {
        println(" -----------email  :::::  $email")
        return validateInputText(firstName, size).fieldIsValid() &&
                validateInputText(name, size).fieldIsValid() &&
                validateInputEmail(email, size).fieldIsValid() &&
                validateInputPassword(password, confirmPassword, size).fieldIsValid()
    }


}