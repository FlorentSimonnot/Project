package com.example.messages

import android.content.Context
import android.content.Intent
import com.example.project.Dialog
import com.example.project.MessagerieActivity
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Discussion(
    val key : String
) {

    fun deleteDiscussionForCurrentUser(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("discussions/$key/messages")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                val messages = p0.children
                val postValues = HashMap<String, Any>()
                messages.forEach {

                    postValues[it.key!!] = it.value!!

                    val message = it.getValue(Message::class.java)
                    val sender = message!!.sender
                    val addressee = message.addressee

                    if(sender.key == SessionUser(context).getIdFromUser()){
                        sender.visible = false
                        postValues["sender"] = sender
                    }
                    else{
                        addressee.visible = false
                        postValues["addressee"] = addressee
                    }
                    ref.child("${it.key}").updateChildren(postValues)

                }
                val intent = Intent(context, MessagerieActivity::class.java)
                Dialog::finish
                context.startActivity(intent)

            }

        })
    }
}