package com.example.events

import kotlin.math.round

class Distance(
    var distance : Double = 0.0
) {

    fun getDistance(bool: Boolean) : Double{
        return if(bool) convertKmToMiles() else distance
    }

    fun getDistanceLong(bool : Boolean) : Long{
        return getDistance(bool).toLong()
    }

    fun convertMetersToKilometers(){
        distance /= 1000.0
    }

    private fun convertKmToMiles() : Double{
        return round(distance*0.621371)
    }


}