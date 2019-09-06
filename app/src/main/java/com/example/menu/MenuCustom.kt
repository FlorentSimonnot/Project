package com.example.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.messages.Message
import com.example.project.*
import com.example.project.R
import com.example.session.SessionUser
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*

class MenuCustom(
    val context: Context,
    val menu : BottomNavigationView,
    val activity : Activity,
    val onNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener
) : FragmentActivity() {

    private val session = SessionUser(context)

    init{
        menu.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    fun setBadges(){
        searchDiscussion()
        searchNotification()
    }


    private fun setBadgeMessages(number : Int){
        val bottomMenu : BottomNavigationMenuView = menu.getChildAt(0) as BottomNavigationMenuView
        val itemMessage : BottomNavigationItemView = bottomMenu.getChildAt(3) as BottomNavigationItemView

        val badge = LayoutInflater.from(context).inflate(R.layout.badge_messages, bottomMenu, false)
        val notif = badge.findViewById<TextView>(R.id.notificationsBadgeTextView)

        itemMessage.removeView(itemMessage.getChildAt(2))

        if(number > 0) {
            notif.visibility = View.VISIBLE
            notif.text = number.toString()
            itemMessage.addView(badge)
        }
    }

    private fun setBadgeNotifications(number : Int){
        val bottomMenu : BottomNavigationMenuView = menu.getChildAt(0) as BottomNavigationMenuView
        val itemNotification : BottomNavigationItemView = bottomMenu.getChildAt(2) as BottomNavigationItemView

        val badge = LayoutInflater.from(context).inflate(R.layout.badge_messages, bottomMenu, false)
        val notif = badge.findViewById<TextView>(R.id.notificationsBadgeTextView)

        itemNotification.removeView(itemNotification.getChildAt(2))

        if(number > 0) {
            notif.visibility = View.VISIBLE
            notif.text = number.toString()
            itemNotification.addView(badge)
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

    private fun searchNotification(){
        val keysNotifications = ArrayList<String>()
        val ref = FirebaseDatabase.getInstance().getReference("notifications/${session.getIdFromUser()}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children
                data.forEach {
                    if(it.hasChild("isSeen")){
                        val key = it.child("isSeen").value as Boolean
                        if(!key)
                            keysNotifications.add(it.key!!)
                    }
                }
                setBadgeNotifications(keysNotifications.size)
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
                            val sortedList = ArrayList(messages.sortedWith(compareBy({it.dateTime})))
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