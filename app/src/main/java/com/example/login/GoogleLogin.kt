package com.example.login

import android.content.Context
import android.content.Intent
import com.example.project.MainActivity
import com.example.project.NextSignInJojoActivity
import com.example.user.Gender
import com.example.user.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleLogin (
    private val googleSignInClient: GoogleSignInClient,
    private val account : GoogleSignInAccount
) : Login {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun login(context: Context) : Boolean {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    if(it.result != null){
                        if(it.result!!.additionalUserInfo.isNewUser){
                            println("FIRST VISIT")
                            val nextSignInJojo = Intent(context, NextSignInJojoActivity::class.java)
                            nextSignInJojo.action = Context.INPUT_SERVICE
                            nextSignInJojo.addCategory("UserSignInWithGoogle")
                            nextSignInJojo.putExtra("firstName", account.givenName)
                            nextSignInJojo.putExtra("name", account.familyName)
                            nextSignInJojo.putExtra("email", account.email)
                            nextSignInJojo.putExtra("password", "")
                            nextSignInJojo.putExtra("uid", user?.uid!!)
                            nextSignInJojo.putExtra("id", account.id)
                            nextSignInJojo.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(nextSignInJojo)
                        }
                        else{
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }
                    }
                    //context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    println("CONNEXION FAILED !!! ${it.result}")
                }

            }
        return true
    }


}