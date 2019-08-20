package com.example.login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.project.MainActivity
import com.example.session.SessionUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class EmailLogin (private val context : Context,
                  private val email : String = "",
                  private val password : String = "") : Login {
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun login(context : Context): Boolean {
        var result = false
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() {
                result = it.isSuccessful
                if(result){
                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener {
                            if(!it.isSuccessful){
                                println("ERRRORRRR")
                            }
                            val session = SessionUser()
                            val token = it.result?.token
                            val ref = FirebaseDatabase.getInstance().getReference("users")
                            ref.child("${session.getIdFromUser()}").child("idTokenRegistration").setValue(token)
                        }
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                else{
                    println("EXCEPTION :::: ${it.exception}")
                    Toast.makeText(context, "Can't login ! ${it.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        return result
    }


}