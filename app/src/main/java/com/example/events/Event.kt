package com.example.events

import com.example.sport.Sport
import com.google.firebase.database.FirebaseDatabase

/**
 * File created by Jonathan CHU on 17/07/19
 */
data class Event (
    val key : String = "",
    var name: String = "",
    var sport: Sport = Sport.INIT,
    var date: String = "",
    var place: String = "",
    var nb_people: Int = 0,
    var description: String = "",
    var privacy : Privacy = Privacy.INIT,
    var creator : String = ""
    ) {


    override fun toString(): String {
        return "${this.sport}   ${this.name}"
    }

    /**insertEvent insert data from event into database
     *
     */
    fun insertEvent(){
        val ref = FirebaseDatabase.getInstance().getReference("events/${this.key}")
        ref.setValue(this)
            .addOnSuccessListener {
                println("DATA INSERTED !!!!")
            }
    }

}
