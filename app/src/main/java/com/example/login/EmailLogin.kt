package com.example.login

import android.content.Context
import android.content.Intent
import com.example.project.MainActivity
import com.google.firebase.auth.FirebaseAuth

class EmailLogin (private val email : String = "", private val password : String = "") : Login {
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun login(context : Context): Boolean {
        var result = false
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() {
                result = it.isSuccessful
                if(result){
                    val intent = Intent(context,   MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
        return result
    }


}