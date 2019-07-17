package com.example.events

import android.content.Context
import com.example.login.EmailLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * File created by Jonathan CHU on 17/07/19
 */
class Event (
    var name: StringBuilder,
    var sport: Sport,
    var date: StringBuilder,
    var place: StringBuilder,
    var nb_people: Int,
    var description: StringBuilder) {


    override fun toString(): String {
        return "${this.sport}   ${this.name}"
    }


    /**insertEvent insert data from event into database
     *
     */
    private fun insertEvent(){
        val ref = FirebaseDatabase.getInstance().getReference("events/")
        ref.setValue(this)
            .addOnSuccessListener {
                println("DATA INSERTED !!!!")
            }
    }
}
