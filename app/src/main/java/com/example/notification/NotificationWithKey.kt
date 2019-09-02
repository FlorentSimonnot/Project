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
        ref.child("notifications").child("${sessionUser.getIdFromUser()}").child("$key").child("isSeen").setValue(true)
    }

    fun removeNotification(context: Context){
        sessionUser = SessionUser(context)
        val deleteNotificationAlertDialog = AlertDialog.Builder(context)
        deleteNotificationAlertDialog.setTitle("You are deleting a notification")
        deleteNotificationAlertDialog.setMessage("Confirm?")
        deleteNotificationAlertDialog.setPositiveButton("Yes") { _, _ ->
            val ref = FirebaseDatabase.getInstance().reference
            ref.child("notifications").child("${sessionUser.getIdFromUser()}").child("$key").removeValue()
        }
        deleteNotificationAlertDialog.setNegativeButton("No"){ _, _ ->

        }
        deleteNotificationAlertDialog.show()
    }

}

