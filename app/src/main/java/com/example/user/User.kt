package com.example.user

import android.content.Context
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.login.EmailLogin
import com.example.notification.Action
import com.example.notification.Notifications
import com.example.project.R
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.sport.SportWithBoolean
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.Serializable
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
    var radius : Int = 0,
    var urlPhoto : String = "",
    var eventsCreated : HashMap<String, Any> = HashMap(),
    var eventsJoined : HashMap<String, Any> = HashMap(),
    var eventsNoted : HashMap<String, Double> = HashMap(),
    var notificationsParam : Notifications = Notifications()

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
                    insertUser(it.result?.user?.uid, context)
                    val emailLogin = EmailLogin(context, email, password)
                    emailLogin.login(context)
                }
            }
        return true
    }

    /**insertUser insert data from user into database
     *
     */
    fun insertUser(uid: String?, context: Context) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.setValue(this)
            .addOnSuccessListener {
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener {
                        if(!it.isSuccessful){
                            println("ERRRORRRR")
                        }
                        val session = SessionUser(context)
                        val token = it.result?.token
                        val ref = FirebaseDatabase.getInstance().getReference("users")
                        ref.child("${session.getIdFromUser()}").child("idTokenRegistration").setValue(token)
                        Sport.values().forEach {
                            if(it != Sport.INIT) {
                                FirebaseDatabase.getInstance().reference.child("sports/$uid/parameters/$it").setValue(true)
                            }
                        }
                    }
            }

    }

    fun showPhotoUser(context: Context, imageView: ImageView, key : String?) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    if(value.urlPhoto.isNotEmpty()) {
                        val refPhoto  = FirebaseStorage.getInstance().getReference("images/${value.urlPhoto}").downloadUrl
                        refPhoto.addOnSuccessListener {
                            Picasso.get().load(it).into(imageView)
                        }
                        refPhoto.addOnFailureListener {
                            Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        if(value.sex == Gender.Male)
                            imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_boy))
                        if(value.sex == Gender.Female)
                            imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_woman))
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    fun writeNotificationUser(context : Context, key : String, textView: TextView, action : Action){
        val ref = FirebaseDatabase.getInstance().getReference("users/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("name").value as String
                val firstName = dataSnapshot.child("firstName").value as String
                when (action) {
                    Action.ACCEPT -> {
                        val s = "<b> $firstName $name </b> have accepted your request ! You are friends"
                        textView.text = Html.fromHtml(s)
                    }
                    else -> "NULL"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun writeBulletNotificationUser(context: Context, imageView: ImageView, action: Action){
        imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_add_user_white))
        when(action){
            Action.ACCEPT -> {
                imageView.setBackgroundResource(R.drawable.rounded_button_primary_color)
            }
            Action.REFUSE -> {
                imageView.setBackgroundResource(R.drawable.rounded_button_accent_color)
            }
            else -> {
                //
            }
        }
    }

    fun writeIdentity(textView: TextView, userKey : String){
        val ref = FirebaseDatabase.getInstance().getReference("users/$userKey")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    textView.text = value.firstName+" "+value.name

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun setEventsOnTabItem(context: Context, tab : TabLayout, keyUser : String){
        val ref = FirebaseDatabase.getInstance().getReference("users/$keyUser/eventsCreated")
        var events = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    events++
                }
                tab.getTabAt(0)?.text = "${context.resources.getString(R.string.events_created)} (${events})"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun setEventsJoinedOnTabItem(context: Context, tab : TabLayout, keyUser: String){
        val ref = FirebaseDatabase.getInstance().getReference("users/$keyUser/eventsJoined")
        var events = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    events++
                }
                tab.getTabAt(1)?.text = "${context.resources.getString(R.string.events_joined)} (${events})"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}


