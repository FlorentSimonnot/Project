package com.example.notification

import android.content.Context
import android.widget.Toast
import com.example.project.NotificationPushActivity
import com.example.project.R
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Notifications(
    val joinEvent: Boolean = true,
    val acceptJoinEvent: Boolean = true,
    val beenInvitedForEvent: Boolean = true,
    val friendInvitation: Boolean = true,
    val acceptInvitationFriend: Boolean = true,
    val modifiedEvent: Boolean = true,
    val messages: Boolean = true
) {

    fun configureSwitches(context: Context, switches : NotificationPushActivity.Switches){
        val ref = FirebaseDatabase.getInstance().getReference("users/${SessionUser(context).getIdFromUser()}/notificationsParam")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    when(it.key){
                        "acceptInvitationFriend" -> {
                            switches.notifUserAccept.isChecked = it.value.toString().toBoolean()
                        }
                        "acceptJoinEvent" -> {
                            switches.notifEventAccept.isChecked = it.value.toString().toBoolean()
                        }
                        "beenInvitedForEvent" -> {
                            switches.notifEventInvitation.isChecked = it.value.toString().toBoolean()
                        }
                        "friendInvitation" -> {
                            switches.notifUserInvitation.isChecked = it.value.toString().toBoolean()
                        }
                        "joinEvent" -> {
                            switches.notifEventJoin.isChecked = it.value.toString().toBoolean()
                        }
                        "modifiedEvent" -> {
                            switches.notifEventModified.isChecked = it.value.toString().toBoolean()
                        }
                        "messages" -> {
                            switches.notifMessages.isChecked = it.value.toString().toBoolean()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun updateSwitches(context: Context, child : String, value : Boolean){
        val ref = FirebaseDatabase.getInstance().getReference("users/${SessionUser(context).getIdFromUser()}/notificationsParam")
        ref.child(child).setValue(value).addOnSuccessListener {
            Toast.makeText(context, context.getString(R.string.notifications_push_toast), Toast.LENGTH_SHORT).show()
        }
    }
}