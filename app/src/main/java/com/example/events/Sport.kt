package com.example.events

enum class Sport() {
    FOOTBALL,
    BASKETBALL,
    CROSSFIT,
    HANDBALL,
    CANOE,
    GOLF,
    INIT; //Add for instantiate sport and use whichSport

    fun whichSport(sport : String) : Sport{
        when(sport){
            "FOOTBALL" -> return Sport.FOOTBALL
            "BASKETBALL" -> return Sport.BASKETBALL
            else -> throw Exception("Oups")
        }
    }
}