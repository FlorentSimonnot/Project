package com.example.user

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.login.EmailLogin
import com.example.project.MainActivity
import com.example.session.SessionUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.io.Serializable
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
    var description : String = "",
    var city : String = "",
    var typeLog : String = "Email",
    var idServiceLog : String = "",
    var privacyAccount: PrivacyAccount = PrivacyAccount.Public,
    var eventsCreated : HashMap<String, String> = HashMap(),
    var eventsJoined : HashMap<String, String> = HashMap(),
    var friends: HashMap<String, String> = HashMap()

): Serializable{

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
        result.put("describe", description)
        result.put("city", city)
        result.put("typeLog", typeLog)
        result.put("idServiceLog", idServiceLog)

        return result
    }

    fun showPhotoUser(context: Context, imageView: ImageView, key : String?) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$key")
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
                                val account = GoogleSignIn.getLastSignedInAccount(context)
                                if(account != null){
                                    Picasso.get()
                                        .load(account.photoUrl)
                                        .into(imageView)
                                }
                            }
                        }
                        else -> {
                            //Picasso.get().load(R.drawable.)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

}


