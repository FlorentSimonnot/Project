package com.example.login

import android.content.Context
import android.content.Intent
import com.example.project.MainActivity
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
                    val userGoogle = User(account.givenName!!, account.familyName!!, account.email!!)
                    userGoogle.insertUser(user?.uid!!)
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    println("CONNEXION FAILED !!! ${it.result}")
                }

            }
        return true
    }


}