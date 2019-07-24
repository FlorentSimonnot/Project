package com.example.form

import com.example.validator.validateInputText

class FormCreateEvent (
    val name : String = "",
    val sport : String = "",
    val date : String = "",
    val place : String = ""
) : Form{
    override fun isFormValid(): Boolean {
        return  validateInputText(name, 255).fieldIsValid() &&
                validateInputText(sport, 255).fieldIsValid()
    }
}