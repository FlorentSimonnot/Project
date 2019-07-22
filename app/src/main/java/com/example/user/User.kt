package com.example.user

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.login.EmailLogin
import com.example.project.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class User(
    var firstName : String = "",
    var name : String = "",
    var email : String = "",
    var password : String = "",
    var sex : Gender = Gender.ALIEN,
    var birthday : String = "",
    var describe : String = "",
    var city : String = "",
    var typeLog : String = "Email",
    var idServiceLog : String = ""
){

    /**createAccount insert into database a new user.
     *
     */
    fun createAccount(auth: FirebaseAuth, context: Context): Boolean {
        var result: Boolean
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() {
                result = it.isSuccessful
                if (result) {
                    insertUser(it.result?.user?.uid)
                    val emailLogin = EmailLogin(email, password)
                    emailLogin.login(context)
                }
            }
        return true
    }

    /**insertUser insert data from user into database
     *
     */
    fun insertUser(uid: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.setValue(this)
            .addOnSuccessListener {
                //Done
            }

    }

    /**
     *
     */
    fun writeInfoUser(uid: String?, textView: TextView, action: String, message: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    when (action) {
                        "name" -> textView.text = "$message ${value.name}"
                        "firstName" -> textView.text = "$message ${value.firstName}"
                        else -> println("ERROR")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

}


