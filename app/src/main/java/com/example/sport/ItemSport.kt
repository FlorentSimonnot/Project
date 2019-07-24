package com.example.sport

class ItemSport(
    private val nameSport : String,
    private val logo : Int) {

    fun getSportName(): String {
        return nameSport
    }

    fun getSportLogo(): Int {
        return logo
    }
}