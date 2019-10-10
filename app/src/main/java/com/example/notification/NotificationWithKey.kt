package com.example.notification

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationWithKey(
    val notification: Notification = Notification(),
    val key : String = ""
) {

    private lateinit var sessionUser: SessionUser

    fun seeNotification(context : Context){
        sessionUser = SessionUser(context)
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("notifications").child(sessionUser.getIdFromUser()).child(key).child("isSeen").setValue(true)
    }

    fun removeNotification(context: Context){
        sessionUser = SessionUser(context)
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("notifications").child(sessionUser.getIdFromUser()).child(key).removeValue()
    }

    fun insertNotification(context: Context){
        sessionUser = SessionUser(context)
        val ref = FirebaseDatabase.getInstance().reference
        println("LALA ${this.notification}")
        ref.child("notifications").child(sessionUser.getIdFromUser()).child(key).child("date").setValue(this.notification.date)
        ref.child("notifications").child(sessionUser.getIdFromUser()).child(key).child("message").setValue(this.notification.message)
        ref.child("notifications").child(sessionUser.getIdFromUser()).child(key).child("isSeen").setValue(this.notification.isSeen)
        ref.child("notifications").child(sessionUser.getIdFromUser()).child(key).child("type").setValue(this.notification.type)
    }



}

