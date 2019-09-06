package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.widget.Toolbar
import com.example.notification.Notifications
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationPushActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var notifUserInvitation : Switch
    private lateinit var notifUserAccept : Switch
    private lateinit var notifEventInvitation : Switch
    private lateinit var notifEventAccept : Switch
    private lateinit var notifEventJoin : Switch
    private lateinit var notifEventModified : Switch
    private lateinit var notifMessages: Switch
    private lateinit var buttonDeleteNotifications : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_push)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Configure notifications"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        notifUserInvitation = findViewById(R.id.user_invitation_switch)
        notifUserAccept= findViewById(R.id.user_accept_switch)
        notifEventInvitation  = findViewById(R.id.event_invitation_switch)
        notifEventAccept = findViewById(R.id.event_accept_switch)
        notifEventJoin = findViewById(R.id.event_join_switch)
        notifEventModified = findViewById(R.id.modified_event_switch)
        notifMessages = findViewById(R.id.messages_switch)
        buttonDeleteNotifications = findViewById(R.id.deleteNotifications)

        notifUserInvitation.setOnClickListener(this)
        notifUserAccept.setOnClickListener(this)
        notifEventAccept.setOnClickListener(this)
        notifEventJoin.setOnClickListener(this)
        notifEventInvitation.setOnClickListener(this)
        notifEventModified.setOnClickListener(this)
        notifMessages.setOnClickListener(this)
        buttonDeleteNotifications.setOnClickListener(this)


        val switches = Switches(
            notifUserInvitation,
            notifUserAccept,
            notifEventInvitation,
            notifEventAccept,
            notifEventJoin,
            notifEventModified,
            notifMessages
        )

        Notifications().configureSwitches(this, switches)

    }

    inner class Switches(
        val notifUserInvitation : Switch,
        val notifUserAccept : Switch,
        val notifEventInvitation : Switch,
        val notifEventAccept : Switch,
        val notifEventJoin : Switch,
        val notifEventModified : Switch,
        val notifMessages: Switch
    )

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.user_accept_switch -> {
                if(notifUserAccept.isChecked){
                    Notifications().updateSwitches(this, "acceptInvitationFriend", true)
                }
                else{
                    Notifications().updateSwitches(this, "acceptInvitationFriend", false)
                }
            }
            R.id.user_invitation_switch -> {
                if(notifUserInvitation.isChecked){
                    Notifications().updateSwitches(this, "friendInvitation", true)
                }
                else{
                    Notifications().updateSwitches(this, "friendInvitation", false)
                }
            }
            R.id.event_join_switch -> {
                if(notifEventJoin.isChecked){
                    Notifications().updateSwitches(this, "joinEvent", true)
                }
                else{
                    Notifications().updateSwitches(this, "joinEvent", false)
                }
            }
            R.id.event_accept_switch -> {
                if(notifEventAccept.isChecked){
                    Notifications().updateSwitches(this, "acceptJoinEvent", true)
                }
                else{
                    Notifications().updateSwitches(this, "acceptJoinEvent", false)
                }
            }
            R.id.event_invitation_switch -> {
                if(notifEventInvitation.isChecked){
                    Notifications().updateSwitches(this, "beenInvitedForEvent", true)
                }
                else{
                    Notifications().updateSwitches(this, "beenInvitedForEvent", false)
                }
            }
            R.id.modified_event_switch -> {
                if(notifEventModified.isChecked){
                    Notifications().updateSwitches(this, "modifiedEvent", true)
                }
                else{
                    Notifications().updateSwitches(this, "modifiedEvent", false)
                }
            }
            R.id.messages_switch -> {
                if(notifMessages.isChecked){
                    Notifications().updateSwitches(this, "messages", true)
                }
                else{
                    Notifications().updateSwitches(this, "messages", false)
                }
            }
            R.id.deleteNotifications -> {
                FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("notifications/${SessionUser(this@NotificationPushActivity).getIdFromUser()}")
                    .removeValue()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
