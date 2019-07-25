package com.example.events

enum class Privacy {
    PUBLIC,
    PRIVATE,
    GUESS,
    INIT;

    fun whichPrivacy(int : Int) : Privacy{
        when(int){
            0 -> return PUBLIC
            1 -> return PRIVATE
            2 -> return GUESS
            else -> throw Exception("Not possible. Verify Privacy")
        }
    }

    fun valueOfString(string : String) : Privacy{
        return when(string){
            "Private" -> PRIVATE
            "Public" -> PUBLIC
            "Only invitation" -> GUESS
            else -> INIT
        }
    }
}