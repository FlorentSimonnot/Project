package com.example.form

import com.example.validator.validateInputInt
import com.example.validator.validateInputText

class FormCreateEvent2(
    val numberOfPeople : Int,
    val description : String,
    val privacy : String
) : Form {

    override fun isFormValid(): Boolean {
        return validateInputText(description, 500).fieldIsValid() &&
                validateInputInt(numberOfPeople, 1, 22).fieldIsValid()
    }
}