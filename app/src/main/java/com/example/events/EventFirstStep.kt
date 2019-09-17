package com.example.events

import com.example.sport.Sport
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class EventFirstStep(
    var name : String = "",
    var sport : Sport = Sport.INIT,
    var date : String = "",
    var time : String = "",
    var place : PlaceEvent = PlaceEvent(""),
    var nbPeople : Int = 0,
    var description : String = "",
    var privacy : Privacy = Privacy.INIT
) : Serializable {

    private fun getFormat() : SimpleDateFormat {
        if(Locale.getDefault().displayLanguage == "한국어"){
            return SimpleDateFormat("yy.MM.dd. HH:mm")
        }
        return SimpleDateFormat("dd/MM/yyyy HH:mm")
    }

    fun getDate() : Long{
        var date = getFormat().parse("$date $time")
        return date.time
    }

}