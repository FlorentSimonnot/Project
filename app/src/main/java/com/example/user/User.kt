package com.example.user

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.login.EmailLogin
import com.example.project.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class User(
    var firstName : String = "",
    var name : String = "",
    var email : String = "",
    var password : String = "",
    var sex : Gender = Gender.Other,
    var birthday : String = "",
    var describe : String = "",
    var city : String = "",
    var typeLog : String = "Email",
    var idServiceLog : String = "",
    var eventsCreated : HashMap<String, String> = HashMap()

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
                    val emailLogin = EmailLogin(context, email, password)
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

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("firstName", firstName)
        result.put("name", name)
        result.put("email", email)
        result.put("password", password)
        result.put("sex", sex)
        result.put("birthday", birthday)
        result.put("describe", describe)
        result.put("city", city)
        result.put("typeLog", typeLog)
        result.put("idServiceLog", idServiceLog)

        return result
    }

}


