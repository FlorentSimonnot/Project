package com.example.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.login.EmailLogin
import com.example.project.LoginActivity
import com.example.user.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class SessionUser{
    val user = FirebaseAuth.getInstance().currentUser

    /** isLogin verify if a user is connected
     *  @return true if user is connected. False else
     *  @author Florent
     */
    fun isLogin() : Boolean{
        return FirebaseAuth.getInstance().uid != null
    }

    /** signOut close the login session.
     *  @author Florent
     */
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    /** getEmailFromUser return the email from current user
     * @return String which is the email of user
     * @exception \throw exception if any user is connected
     * @author Florent
     */
    fun getEmailFromUser() : String{
        if(user == null){
            throw Exception("User not connected")
        }
        return user.email.toString()
    }

    /** getIdFromUser return the id from current user
     * @return String which is the id of user
     * @exception \throw exception if any user is connected
     * @author Florent
     */
    fun getIdFromUser() : String{
        if(user == null){
            throw Exception("User not connected")
        }
        return user.uid.toString()
    }

    fun login(email: String, password : String, context: Context){
        if(user != null){
            throw Exception("Impossible")
        }
        else{
            val emailLogin = EmailLogin(email, password)
            emailLogin.login(context)
        }
    }

    /**deleteUser delete the current user from database
     * @param context for intent
     * @author Florent
     */
    fun deleteUser(context: Context){
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("DELETE ACCOUNT WITH SUCCESS")
                    val database = FirebaseDatabase.getInstance().reference
                    database.child("users").child(user.uid).removeValue()
                    val intent : Intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                else{
                    println("ERROR : ${task.exception}")
                }
            }
    }

    /**resetPassword send an email to reset password.
     *
     */
    fun resetPassword(context: Context, auth : FirebaseAuth, emailAddress : String){
        if(user == null){
            auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Check your email now ;)", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun writeInfoUser(uid: String?, textView: TextView, action: String, message: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    when (action) {
                        "firstName" -> textView.text = "$message${value.firstName}"
                        "name" -> textView.text = "$message${value.name}"
                        "email" -> textView.text = "$message${value.email}"
                        "password" -> textView.text = "$message${value.password}"
                        "sex" -> textView.text = "$message${value.sex}"
                        "birthday" -> textView.text = "$message${value.birthday}"
                        "describe" -> textView.text = "$message${value.describe}"
                        "city" -> textView.text = "$message${value.city}"
                        else -> println("ERROR")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    fun showPhotoUser(context: Context, imageView: ImageView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${user?.uid}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    when(value.typeLog){
                        "Facebook" ->{
                            if(value.idServiceLog.isNotEmpty()){
                                Picasso.get()
                                    .load("https://graph.facebook.com/" + value.idServiceLog+ "/picture?type=large")
                                    .into(imageView);
                            }
                        }
                        "Google" -> {
                            if(value.idServiceLog.isNotEmpty()){
                                var account = GoogleSignIn.getLastSignedInAccount(context)
                                if(account != null){
                                    Picasso.get()
                                        .load(account.photoUrl)
                                        .into(imageView);
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

}