package com.example.form

import com.example.validator.ValidateField
import com.example.validator.validateInputEmail

class FormResetPassword(
    private val email : String = ""
) : Form {

    override fun isFormValid(): Boolean {
        return validateInputEmail(email, 255).fieldIsValid()
    }
}