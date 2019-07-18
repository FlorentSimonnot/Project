package com.example.events

import android.content.Context
import com.example.login.EmailLogin
import com.example.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * File created by Jonathan CHU on 17/07/19
 */
class Event (
    var name: String,
    var sport: Sport,
    var date: String,
    var place: String,
    var nb_people: Int,
    var description: String,
    var privacy : Privacy) {

    private val key : String = Utils().generatePassword(70)

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
