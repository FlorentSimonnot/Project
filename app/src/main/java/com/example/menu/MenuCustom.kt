package com.example.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import com.example.messages.Message
import com.example.project.ActivityInfoUser
import com.example.project.MainActivity
import com.example.project.MessagerieActivity
import com.example.project.R
import com.example.session.SessionUser
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class MenuCustom(
    val context: Context,
    val menu : BottomNavigationView,
    val activity : Activity
) {

    private val session = SessionUser()

    fun setBadges(){
        searchDiscussion()
    }

    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val checkAccountIntent = Intent(context, MainActivity::class.java)
                context.startActivity(checkAccountIntent)
                activity.overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                val checkAccountIntent = Intent(context, ActivityInfoUser::class.java)
                context.startActivity(checkAccountIntent)
                activity.overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                val checkAccountIntent = Intent(context, MessagerieActivity::class.java)
                context.startActivity(checkAccountIntent)
                activity.overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun setBadgeMessages(number : Int){
        val itemMessage : BottomNavigationItemView = menu.findViewById(R.id.navigation_chat)
        val badge = LayoutInflater.from(context).inflate(R.layout.badge_messages, itemMessage, false)
        val notif = badge.findViewById<TextView>(R.id.notificationsBadgeTextView)
        if(number > 0) {
            notif.visibility = View.VISIBLE
            notif.text = number.toString()
            itemMessage.addView(badge)
        }
        else{
            itemMessage.removeView(badge)
        }
    }

    private fun searchDiscussion(){
        val keysChat = ArrayList<String>()
        val ref = FirebaseDatabase.getInstance().getReference("friends/${session.getIdFromUser()}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children
                data.forEach {
                    if(it.hasChild("keyChat")){
                        val key = it.child("keyChat").value as String
                        keysChat.add(key)
                    }
                }
                searchDiscussionNotSeen(keysChat)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchDiscussionNotSeen(keysChat : ArrayList<String>){
        var number = 0
        keysChat.forEachIndexed { index, it ->
            val ref = FirebaseDatabase.getInstance().getReference("discussions/$it")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    //Nothing
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val messages = ArrayList<Message>()
                    if(p0.hasChild("isSeen")){
                        if(!(p0.child("isSeen").value as Boolean)){
                            val data = p0.child("messages").children
                            data.forEach {
                                val message = it.getValue(Message::class.java)
                                messages.add(message!!)
                            }
                            val sortedList = ArrayList(messages.sortedWith(compareBy({it.date}, {it.time})))
                            if(sortedList.size > 0) {
                                val lastMessage = sortedList[sortedList.size - 1]
                                if(lastMessage.addressee.key == session.getIdFromUser())
                                    number++
                            }
                        }
                    }

                    if(index == keysChat.size - 1){
                        setBadgeMessages(number)
                    }
                }

            })
        }

    }



}