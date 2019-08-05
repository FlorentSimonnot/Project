package com.example.form

import com.example.dateCustom.DateCustom
import com.example.validator.validateInputText

class FormCreateEvent (
    val name : String = "",
    val sport : String = "",
    val date : String = "",
    val time : String = "",
    val place : String? = ""
) : Form{
    override fun isFormValid(): Boolean {
        return  validateInputText(name, 255).fieldIsValid() &&
                validateInputText(sport, 255).fieldIsValid() &&
                DateCustom(date).isAfter(DateCustom("00/00/0000").getCurrentDate())
    }
}