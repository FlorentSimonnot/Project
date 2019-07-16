package com.example.user

import android.util.Log
import com.example.project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

data class User (
    val firstName : String = "",
    val name : String = "",
    val email : String = "",
    val password : String = "",
    val sex : Gender,
    val birthday : String = "",
    val describe : String = "",
    val city : String = ""
){

    /**createAccount insert into database a new user.
     *
     */
    fun createAccount(auth : FirebaseAuth) : Boolean{
        var res = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() {
                if(it.isSuccessful){
                    println("Add successfully + ${it.result?.user?.uid}")
                }
                else{
                    println("WHY ? " + it.exception?.message)
                }

            }
        return res
    }

}
