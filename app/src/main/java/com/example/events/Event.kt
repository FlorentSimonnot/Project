package com.example.events

import android.content.Context
import com.example.login.EmailLogin
import com.example.user.User
import com.example.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
