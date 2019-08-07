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

    override fun toString(): String {
        return when(this){
            PRIVATE -> "private"
            PUBLIC -> "public"
            GUESS -> "only with invitation"
            else -> {
                throw Exception("Probleme with value of Privacy")
            }
        }
    }
}