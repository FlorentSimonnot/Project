package com.example.messages

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

data class Message(
    val idMessage : String = "",
    val sender : String = "",
    val addressee : String = "",
    val text : String = "",
    val date : String = "",
    val time : String = "",
    val isSee : Boolean = false
) {
    fun insertMessage(context : Context, editText: EditText){
        val ref = FirebaseDatabase.getInstance().getReference("messages/$sender/$addressee/$idMessage")
        ref.setValue(this).addOnSuccessListener {
            Toast.makeText(context, "Message send !", Toast.LENGTH_SHORT).show()
            editText.setText("")
        }

    }

    fun deleteMessage(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("messages/$sender/$addressee/$idMessage")
        ref.removeValue().addOnSuccessListener {
            Toast.makeText(context, "Message delete !", Toast.LENGTH_SHORT).show()
        }
    }
}