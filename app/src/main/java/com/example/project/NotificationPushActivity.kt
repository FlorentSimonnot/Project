package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import com.example.notification.Notifications

class NotificationPushActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var notifUserInvitation : Switch
    private lateinit var notifUserAccept : Switch
    private lateinit var notifEventInvitation : Switch
    private lateinit var notifEventAccept : Switch
    private lateinit var notifEventJoin : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_push)

        notifUserInvitation = findViewById(R.id.user_invitation_switch)
        notifUserAccept= findViewById(R.id.user_accept_switch)
        notifEventInvitation  = findViewById(R.id.event_invitation_switch)
        notifEventAccept = findViewById(R.id.event_accept_switch)
        notifEventJoin = findViewById(R.id.event_join_switch)
        notifUserInvitation.setOnClickListener(this)
        notifUserAccept.setOnClickListener(this)
        notifEventAccept.setOnClickListener(this)
        notifEventJoin.setOnClickListener(this)
        notifEventInvitation.setOnClickListener(this)


        val switches = Switches(
            notifUserInvitation,
            notifUserAccept,
            notifEventInvitation,
            notifEventAccept,
            notifEventJoin
        )

        Notifications().configureSwitches(this, switches)

    }

    inner class Switches(
        val notifUserInvitation : Switch,
        val notifUserAccept : Switch,
        val notifEventInvitation : Switch,
        val notifEventAccept : Switch,
        val notifEventJoin : Switch
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
        }
    }

}
