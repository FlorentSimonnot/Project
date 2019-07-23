package com.example.sport

import com.example.project.R

enum class Sport() {
    FOOTBALL,
    BASKETBALL,
    CROSSFIT,
    HANDBALL,
    CANOE,
    GOLF,
    INIT; //Add for instantiate sport and use whichSport

    fun whichSport(sport : String) : Sport {
        return when(sport){
            "FOOTBALL" -> FOOTBALL
            "BASKETBALL" -> BASKETBALL
            "GOLF" -> GOLF
            "HANDBALL" -> HANDBALL
            else -> {println(" SPORT !!! : ${sport}"); return INIT}
        }
    }

    fun getLogo() : Int{
        return when(this){
            FOOTBALL -> R.drawable.ic_football
            BASKETBALL -> R.drawable.ic_basketball
            HANDBALL -> R.drawable.handball_color
            GOLF -> R.drawable.ic_golf
            CANOE -> R.drawable.canoe_color
            CROSSFIT -> R.drawable.canoe_color
            else -> R.drawable.ic_not_found
        }
    }
}