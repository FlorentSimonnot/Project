package com.example.messages

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.dateCustom.DateCustom
import com.example.dateCustom.DateUTC
import com.example.dateCustom.TimeCustom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

data class Message(
    val idMessage : String = "",
    var sender : Actor = Actor(),
    var addressee : Actor = Actor(),
    val text : String = "",
    val dateTime : Long = 0,
    val typeMessage: TypeMessage = TypeMessage.TEXT,
    var urlPhoto : String = ""
) : Serializable {

    fun insertMessage(context : Context, editText: EditText){
        val refKeyChat = FirebaseDatabase.getInstance().getReference("friends/${sender.key}/${addressee.key}")
        refKeyChat.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.child("keyChat").value as String
                if(value != null){
                    if(typeMessage == TypeMessage.IMAGE){
                        this@Message.urlPhoto = "images/discussions/$value/${this@Message.urlPhoto}"
                    }
                    val ref = FirebaseDatabase.getInstance().getReference("discussions/$value/messages/${this@Message.idMessage}")
                    ref.setValue(this@Message).addOnSuccessListener {
                        editText.setText("")
                    }
                    ref.parent!!.parent!!.child("isSeen").setValue(false)
                }
            }

        })

    }

    fun insertMessageChatEvent(context : Context, editText: EditText, keyEvent: String){
        if(typeMessage == TypeMessage.IMAGE){
            this@Message.urlPhoto = "images/discussions/$keyEvent/${this@Message.urlPhoto}"
        }
        val ref = FirebaseDatabase.getInstance().getReference("chatEvent/$keyEvent/messages/${this@Message.idMessage}")
        ref.setValue(this@Message).addOnSuccessListener {
            editText.setText("")
        }
    }

    fun deleteMessage(context: Context){
        val deleteMessageAlertDialog = AlertDialog.Builder(context)
        deleteMessageAlertDialog.setTitle("You are deleting your message")
        deleteMessageAlertDialog.setMessage("Confirm?")
        deleteMessageAlertDialog.setPositiveButton("Yes"){ _, _ ->
            val refKeyChat = FirebaseDatabase.getInstance().getReference("friends/${sender.key}/${addressee.key}")
            refKeyChat.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //Nothing
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val value = p0.child("keyChat").value
                    if (value != null) {
                        val ref =
                            FirebaseDatabase.getInstance().getReference("discussions/$value/messages/${this@Message.idMessage}")
                        ref.removeValue()
                    }
                }

            })
        }
        deleteMessageAlertDialog.setNegativeButton("No"){ _, _ ->

        }
        deleteMessageAlertDialog.show()

    }

    fun deleteMessageChatEvent(context: Context, keyChat: String){
        val deleteMessageAlertDialog = AlertDialog.Builder(context)
        deleteMessageAlertDialog.setTitle("You are deleting your message")
        deleteMessageAlertDialog.setMessage("Confirm?")
        deleteMessageAlertDialog.setPositiveButton("Yes"){ _, _ ->
            val ref = FirebaseDatabase.getInstance().getReference("chatEvents/$keyChat/messages/${this@Message.idMessage}")
            ref.removeValue()
        }
        deleteMessageAlertDialog.setNegativeButton("No"){ _, _ ->

        }
        deleteMessageAlertDialog.show()

    }

    fun writeDate() : String{
        val currentDate = DateUTC(dateTime)
        if(currentDate.isToday()){
            return currentDate.showTime()
        }
        if(currentDate.isYesterday()){
            return "Yesterday"
        }
        else{
            return currentDate.showDate()
        }
    }

    fun seeMessage(keyChat : String){
        val ref = FirebaseDatabase.getInstance().getReference("discussions/$keyChat/isSeen")
        ref.setValue(true)
    }

}