package com.example.form

import com.example.validator.validateInputText

class FormCreateEvent (
    private val name : String = "",
    private val sport : String = "",
    private val date : String = "",
    private val place : String = "",
    private val number : Int = 0,
    private val describe : String = "",
    private val privacy : String = ""
) : Form{
    override fun isFormValid(): Boolean {
        return validateInputText(describe, 255).fieldIsValid() &&
                validateInputText(name, 255).fieldIsValid() &&
                validateInputText(sport, 255).fieldIsValid()
    }
}