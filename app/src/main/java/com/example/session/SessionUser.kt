package com.example.session

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.login.EmailLogin
import com.example.project.MainActivity
import com.example.user.Gender
import com.example.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class SessionUser {
    private val user = FirebaseAuth.getInstance().currentUser
    var currentUser : User = User()

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

}