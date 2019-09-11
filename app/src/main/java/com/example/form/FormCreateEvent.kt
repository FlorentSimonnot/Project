package com.example.form

import com.example.dateCustom.DateCustom
import com.example.dateCustom.TimeCustom
import com.example.events.PlaceEvent
import com.example.events.Privacy
import com.example.validator.validateInputText
import java.text.SimpleDateFormat
import java.util.*

class FormCreateEvent (
    val name : String = "",
    val sport : String = "",
    val date : String = "",
    val time : String = "",
    val place : PlaceEvent = PlaceEvent(""),
    val description : String? = "",
    val nbPeople : Int = 0,
    val privacy : Privacy = Privacy.INIT
) : Form{
    override fun isFormValid(): Boolean {
        println("THIS : ${this.toString()}")
        return  validateInputText(name, 255).fieldIsValid() &&
                validateInputText(sport, 255).fieldIsValid() &&
                nbPeople > 0 && place.idPlace!!.isNotEmpty() && description!!.isNotEmpty()
    }

    override fun toString(): String {
        return "$name | $sport |$date | $time | $place | $description | $nbPeople | ${privacy.name}"
    }

    fun getFormat() : SimpleDateFormat{
        if(Locale.getDefault().displayLanguage == "한국어"){
            return SimpleDateFormat("yyyy/MM/dd HH:mm")
        }
        return SimpleDateFormat("dd/MM/yyyy HH:mm")
    }

    fun getDate() : Long{
        var date = getFormat().parse("$date $time")
        return date.time
    }
}