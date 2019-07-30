package com.example.form

import com.example.validator.validateInputEmail
import com.example.validator.validateInputText

class FormLoginEmail(
    private val email : String = "",
    private val password : String = ""
) : Form {

    override fun isFormValid(): Boolean {
        return validateInputEmail(email, 255).fieldIsValid() &&
                validateInputText(password, 255).fieldIsValid()
    }
}