package com.example.form

import com.example.user.Gender
import com.example.validator.validateInputText
data class FormSignInSecond (
    val sex : Gender,
    val birthday : String = "",
    val describe : String = "",
    val city : String = "") : Form{

    override fun isFormValid(): Boolean {
        return validateInputText(describe, 255).fieldIsValid() &&
                validateInputText(birthday, 255).fieldIsValid() &&
                validateInputText(city, 255).fieldIsValid()
    }



}