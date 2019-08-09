package com.example.session

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.login.EmailLogin
import com.example.place.SessionGooglePlace
import com.example.project.LoginActivity
import com.example.user.Gender
import com.example.user.User
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

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
            val emailLogin = EmailLogin(context, email, password)
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

    fun writeInfoUser(context: Context, uid: String?, textView: TextView, action: String) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    when (action) {
                        "identity" -> textView.text = value.firstName+" "+value.name
                        "firstName" -> textView.text = value.firstName
                        "name" -> textView.text = value.name
                        "email" -> textView.text = value.email
                        "password" -> textView.text = value.password
                        "sex" -> textView.text = "${value.sex
                        }"
                        "birthday" -> textView.text = value.birthday
                        "describe" -> textView.text = value.description
                        "city" -> {
                            //INIT GOOGLE PLACE
                            //Init google place
                            val gg = SessionGooglePlace(context)
                            gg.init()
                            val placesClient = gg.createClient()

                            //Search place in according to the ID
                            val placeId : String = value.city
                            val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME)
                            val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)


                            placesClient.fetchPlace(request)
                                .addOnSuccessListener {
                                    val place : Place = it.place
                                    textView.text = place.name
                                }
                                .addOnFailureListener {
                                    //textView1.text = it.message
                                    Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
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
                                val account = GoogleSignIn.getLastSignedInAccount(context)
                                if(account != null && account.id == getIdFromUser()){
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

    fun identity(uid: String?, textView: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    var builder = StringBuilder()
                    builder.append(value.firstName).append(" ").append(value.name)
                    textView.text = builder
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun setNewPassword(oldPassword: String, newPassword: String) {
        val ref = FirebaseDatabase.getInstance().getReference("users").child("${user?.uid}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    val postValues = HashMap<String, Any>()
                    dataSnapshot.children.forEach {
                        postValues.put(it.key!!, it.value!!)
                    }
                    if(oldPassword == postValues["password"]){
                        //Update firebase
                        user?.updatePassword(newPassword)
                        //Update Database
                        postValues.put("password", newPassword)
                        ref.updateChildren(postValues)
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun updateAccount(
        newEmail: String,
        newSex: Gender,
        newBirthday: String,
        newCity: String,
        newDescription: String
    ) {
        val ref = FirebaseDatabase.getInstance().getReference("users").child("${user?.uid}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    val postValues = HashMap<String, Any>()
                    dataSnapshot.children.forEach {
                        postValues.put(it.key!!, it.value!!)
                    }

                    val newName = value.name
                    val newFirstName = value.firstName

                    //Update firebase
                    user?.updateEmail(newEmail)
                    //Update Database
                    postValues.put("name", newName)
                    postValues.put("firstName", newFirstName)
                    postValues.put("email", newEmail)
                    postValues.put("sex", newSex)
                    postValues.put("birthday", newBirthday)
                    postValues.put("city", newCity)
                    postValues.put("describe", newDescription)
                    ref.updateChildren(postValues)

                    value.name = newName
                    value.firstName = newFirstName
                    value.email = newEmail
                    value.sex = newSex
                    value.birthday = newBirthday
                    value.city = newCity
                    value.description = newDescription

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun countFriends(friendsTextView: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/friends")
        var friends = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.value == "friend"){
                        friends++
                    }
                }
                friendsTextView.text = friends.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun countWaiting(waitingTextView: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/friends")
        var waiting = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.value == "waiting"){
                        waiting++
                    }
                }
                waitingTextView.text = waiting.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun countCreated(createdTextView: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/eventsCreated")
        var created = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    created++
                }
                createdTextView.text = created.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun countJoined(joinedTextView: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/eventsJoined")
        var joined = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    joined++
                }
                joinedTextView.text = joined.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun acceptFriend(ctx: Context, userKey: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/friends/$userKey")
        ref.setValue("friend")
        Toast.makeText(ctx, "You have accepted this user as friend :)", Toast.LENGTH_SHORT).show()
    }

    fun refuseFriend(ctx: Context, userKey: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/friends/$userKey")
        ref.removeValue()
        Toast.makeText(ctx, "You have refused this user as friend :(", Toast.LENGTH_SHORT).show()
    }
    fun deleteFriend(ctx: Context, userKey: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/friends/$userKey")
        ref.removeValue()
        Toast.makeText(ctx, "You have refused this user as friend :(", Toast.LENGTH_SHORT).show()
    }


}