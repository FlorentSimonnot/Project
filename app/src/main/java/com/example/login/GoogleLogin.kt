package com.example.login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.project.HomeActivity
import com.example.project.NextSignInJojoActivity
import com.example.session.SessionUser
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

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
                            FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener {
                                    if(!it.isSuccessful){
                                        println("ERRRORRRR")
                                    }
                                    val session = SessionUser(context)
                                    val token = it.result?.token
                                    val ref = FirebaseDatabase.getInstance().getReference("users")
                                    ref.child("${session.getIdFromUser()}").child("idTokenRegistration").setValue(token)
                                }
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    }
                } else {
                    println("CONNEXION FAILED !!! ${it.result}")
                    Toast.makeText(context, "Can't login ! ${it.exception}", Toast.LENGTH_SHORT).show()
                }

            }
        return true
    }


}