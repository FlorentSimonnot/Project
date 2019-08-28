package com.example.messages

import android.graphics.Typeface
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DiscussionViewLastMessage(
    val keyChat : String,
    val lastMessage: Message
){

    fun discussionIsNotSeen(textView: TextView, time : TextView, relativeLayout: RelativeLayout){
        val ref = FirebaseDatabase.getInstance().getReference("discussions/$keyChat")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //No
            }

            override fun onDataChange(p0: DataSnapshot) {
                val isSeen : Boolean = p0.child("isSeen").value as Boolean
                if(!isSeen){
                    textView.setTypeface(null, Typeface.BOLD)
                    time.setTypeface(null, Typeface.BOLD)
                    relativeLayout.visibility = View.VISIBLE
                }
                else{
                    textView.setTypeface(null, Typeface.NORMAL)
                    time.setTypeface(null, Typeface.NORMAL)
                    relativeLayout.visibility = View.GONE
                }
            }

        })
    }
}