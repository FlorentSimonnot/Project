package com.example.messages

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.example.dateCustom.DateCustom
import com.example.dateCustom.TimeCustom
import com.example.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

data class Message(
    val idMessage : String = "",
    val sender : String = "",
    val addressee : String = "",
    val text : String = "",
    val date : String = "",
    val time : String = "",
    val isSee : Boolean = false,
    val typeMessage: TypeMessage = TypeMessage.TEXT,
    val urlPhoto : String = ""
) : Serializable {

    fun insertMessage(context : Context, editText: EditText){
        val refKeyChat = FirebaseDatabase.getInstance().getReference("users/$sender/friends/$addressee")
        refKeyChat.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.child("keyChat").value
                if(value != null){
                    val ref = FirebaseDatabase.getInstance().getReference("discussions/$value/${this@Message.idMessage}")
                    ref.setValue(this@Message).addOnSuccessListener {
                        editText.setText("")
                    }
                }
            }

        })

    }

    fun deleteMessage(context: Context){
        val refKeyChat = FirebaseDatabase.getInstance().getReference("users/$sender/friends/$addressee")
        refKeyChat.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.child("keyChat").value
                if (value != null) {
                    val ref =
                        FirebaseDatabase.getInstance().getReference("discussions/$value/${this@Message.idMessage}")
                    ref.removeValue()
                }
            }

        })
    }

    fun writeDate() : String{
        val currentDate = DateCustom("00/00/0000").getCurrentDate()
        if(DateCustom(this.date).isEqual(currentDate)){
            return TimeCustom(this.time).showTime()
        }
        if(DateCustom(this.date).isYesterday()){
            return "Hier"
        }
        else{
            return DateCustom(this.date).toString()
        }
    }
}