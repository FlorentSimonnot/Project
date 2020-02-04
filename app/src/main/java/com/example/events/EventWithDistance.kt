package com.example.events

import java.io.Serializable

class EventWithDistance(
    val event : Event = Event(),
    val distance : Float = 0.0f
) : Serializable {
}