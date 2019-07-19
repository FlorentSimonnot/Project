package com.example.events

import com.example.project.R

enum class Sport() {
    FOOTBALL,
    BASKETBALL,
    CROSSFIT,
    HANDBALL,
    CANOE,
    GOLF,
    INIT; //Add for instantiate sport and use whichSport

    fun whichSport(sport : String) : Sport{
        return when(sport){
            "FOOTBALL" -> FOOTBALL
            "BASKETBALL" -> BASKETBALL
            "GOLF" -> GOLF
            "HANDBALL" -> HANDBALL
            else -> throw Exception("Oups")
        }
    }

    fun getLogo() : Int{
        return when(this){
            FOOTBALL -> R.drawable.foot_color
            BASKETBALL -> R.drawable.basket_color
            HANDBALL -> R.drawable.handball_color
            GOLF -> R.drawable.golf_color
            CANOE -> R.drawable.canoe_color
            else -> -1
        }
    }
}