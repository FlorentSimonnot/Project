package com.example.sport

import com.example.project.R

enum class Sport() {
    FOOTBALL,
    BASKETBALL,
    CROSSFIT,
    HANDBALL,
    CANOE,
    GOLF,
    MUSCULATION,
    TENNISDETABLE,
    TENNIS,
    TRAIL,
    INIT; //Add for instantiate sport and use whichSport

    fun getLogo() : Int{
        return when(this){
            FOOTBALL -> R.drawable.ic_football
            BASKETBALL -> R.drawable.ic_basketball
            HANDBALL -> R.drawable.ic_handball
            CROSSFIT -> R.drawable.ic_crossfit
            GOLF -> R.drawable.ic_golf
            CANOE -> R.drawable.ic_canoe
            MUSCULATION -> R.drawable.ic_musculation
            TENNIS -> R.drawable.ic_tennis
            TRAIL -> R.drawable.ic_trail
            TENNISDETABLE -> R.drawable.ic_ping_pong
            else -> R.drawable.ic_not_found
        }
    }
}