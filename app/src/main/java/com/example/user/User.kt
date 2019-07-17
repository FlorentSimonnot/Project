package com.example.user

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.example.login.EmailLogin
import com.example.project.MainActivity
import com.example.project.NextSignInJojoActivity
import com.example.project.R
import com.example.session.SessionUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.*

data class User (
    val firstName : String = "",
    val name : String = "",
    val email : String = "",
    val password : String = "",
    val sex : Gender = Gender.ALIEN,
    val birthday : String = "",
    val describe : String = "",
    val city : String = ""
){

    /**createAccount insert into database a new user.
     *
     */
    fun createAccount(auth : FirebaseAuth, context : Context) : Boolean{
        var result : Boolean
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() {
                result = it.isSuccessful
                if(result){
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
    private fun insertUser(uid : String?){
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.setValue(this)
            .addOnSuccessListener {
                println("DATA INSERTED !!!!")
            }

    }


}

