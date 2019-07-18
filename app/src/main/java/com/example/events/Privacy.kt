package com.example.events

enum class Privacy {
    PUBLIC,
    PRIVATE,
    INIT;

    fun whichPrivacy(int : Int) : Privacy{
        when(int){
            0 -> return PUBLIC
            1 -> return PRIVATE
            else -> throw Exception("Not possible. Verify Privacy")
        }
    }
}