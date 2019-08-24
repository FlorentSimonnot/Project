package com.example.events

enum class Privacy {
    PUBLIC,
    PRIVATE,
    GUESS,
    INIT;

    fun valueOfString(string : String) : Privacy{
        return when(string){
            "Private" -> PRIVATE
            "PRIVATE" -> PRIVATE
            "Public" -> PUBLIC
            "PUBLIC" -> PUBLIC
            "Only invitation" -> GUESS
            "ONLY INVITATION" -> GUESS
            else -> INIT
        }
    }

    override fun toString(): String {
        return when(this){
            PRIVATE -> "Private"
            PUBLIC -> "Public"
            GUESS -> "Only with invitation"
            else -> {
                throw Exception("Probleme with value of Privacy")
            }
        }
    }
}