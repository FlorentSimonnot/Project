package com.example.events

import com.google.android.gms.maps.model.MarkerOptions

class EventAndMarker(
    val event : Event,
    val marker : MarkerOptions
) {

    override fun equals(other: Any?): Boolean {
        val two = other as EventAndMarker
        if(two.marker == marker)
            return true
        return false
    }

}