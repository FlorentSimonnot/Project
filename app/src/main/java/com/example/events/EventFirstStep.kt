package com.example.events

import com.example.sport.Sport
import java.io.Serializable

data class EventFirstStep(
    var name : String = "",
    var sport : Sport = Sport.INIT,
    var date : String = "",
    var time : String = "",
    var place : String ="",
    var nbPeople : Int = 0,
    var description : String = "",
    var privacy : Privacy = Privacy.INIT
) : Serializable {
}