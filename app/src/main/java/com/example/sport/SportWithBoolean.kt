package com.example.sport

import com.google.firebase.database.FirebaseDatabase

class SportWithBoolean(
    val sport: Sport = Sport.INIT,
    var boolean: Boolean = true
) {

    fun changeBoolean(uid : String){
        if(boolean) {
            FirebaseDatabase.getInstance().reference
                .child("sports")
                .child(uid)
                .child("parameters")
                .child("$sport")
                .setValue(false)
        }else{
            FirebaseDatabase.getInstance().reference
                .child("sports")
                .child(uid)
                .child("parameters")
                .child("$sport")
                .setValue(true)
        }

    }
}