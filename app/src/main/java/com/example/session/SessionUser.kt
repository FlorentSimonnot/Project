package com.example.session

import androidx.appcompat.app.ActionBar
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.renderscript.Sampler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.example.login.EmailLogin
import com.example.place.SessionGooglePlace
import com.example.project.LoginActivity
import com.example.project.R
import com.example.user.Gender
import com.example.user.User
import com.example.utils.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_about_us.view.*
import java.io.Serializable
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

class SessionUser(val context: Context) : Serializable{
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
            context.startActivity(Intent(context, LoginActivity::class.java))
            return ""
        }
        return user!!.uid.toString()
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

    fun writeNameUserDiscussion(context: Context, uid: String?, supportActionBar: ActionBar){
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    println("VALUE ${value.firstName}")
                    supportActionBar.title = "${value.firstName} ${value.name}"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    fun writeRadius(context: Context, uid: String?, seekBar: SeekBar, textView: TextView){
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("radius")) {
                    val arround = dataSnapshot.child("radius").value as Long
                    seekBar.progress = arround.toInt()
                    textView.text = "$arround km"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

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
                        "firstNameButton" -> textView.text = "${context.resources.getString(R.string.learn_more)} ${value.firstName}"
                        "name" -> textView.text = value.name
                        "email" -> textView.text = value.email
                        "password" -> textView.text = value.password
                        "sex" -> textView.text = "${value.sex}"
                        "birthday" -> textView.text = value.birthday
                        "describe" -> textView.text = value.description
                        "deleteFriend" -> textView.text = "Supprimer ${value.firstName} ${value.name} de ses amis"
                        "sendMessage" -> textView.text = "Envoyé un message à ${value.firstName} ${value.name}"
                        "deleteFromEvent" -> textView.text = "Retirer ${value.firstName} ${value.name} de l'évènement"
                        "AcceptJoinEvent" -> textView.text = "Accepter ${value.firstName} ${value.name}"
                        "RefuseJoinEvent" -> textView.text = "Refuser ${value.firstName} ${value.name}"
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
                    if(value.urlPhoto != null) {
                        val refPhoto  = FirebaseStorage.getInstance().getReference("images/${value.urlPhoto}").downloadUrl
                        refPhoto.addOnSuccessListener {
                            Picasso.get().load(it).into(imageView)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    fun setButtonFriend(buttonAddFriend : Button, buttonRemoveFriend : Button, buttonCancelFriend : Button, uid: String){
        val ref = FirebaseDatabase.getInstance().getReference("friends/$uid")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                buttonAddFriend.visibility = View.VISIBLE
                buttonCancelFriend.visibility = View.GONE
                buttonRemoveFriend.visibility = View.GONE
                p0.children.forEach {
                    if(it.key == getIdFromUser()){
                        when(it.child("status").value){
                            "friend" -> {
                                buttonAddFriend.visibility = View.GONE
                                buttonCancelFriend.visibility = View.GONE
                                buttonRemoveFriend.visibility = View.VISIBLE
                            }
                            "waiting" -> {
                                buttonAddFriend.visibility = View.GONE
                                buttonCancelFriend.visibility = View.VISIBLE
                                buttonRemoveFriend.visibility = View.GONE
                            }
                        }
                    }
                }
            }

        })
    }

    fun identity(uid: String?, textView: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value : User? = dataSnapshot.getValue(User::class.java)
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
        city: String,
        newDescription: String,
        photo : Uri?,
        radius : Int
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
                    var newCity = ""
                    if(city.isEmpty()){
                        newCity = value.city
                    }
                    else{
                        newCity = city
                    }
                    var urlPhoto = ""
                    val refPhotoStorage = FirebaseStorage.getInstance().getReference("/images")
                    if(photo == null){
                        urlPhoto = value.urlPhoto
                    }
                    else{
                        urlPhoto = UUID.randomUUID().toString()
                        if(value.urlPhoto.isNotEmpty()){
                            refPhotoStorage.child(value.urlPhoto).delete()
                        }
                        refPhotoStorage.child(urlPhoto).putFile(photo)
                    }

                    //Update firebase
                    user?.updateEmail(newEmail)
                    //Update Database
                    postValues.put("name", newName)
                    postValues.put("firstName", newFirstName)
                    postValues.put("email", newEmail)
                    postValues.put("sex", newSex)
                    postValues.put("birthday", newBirthday)
                    postValues.put("city", newCity)
                    postValues.put("description", newDescription)
                    postValues.put("urlPhoto", urlPhoto)
                    postValues.put("radius", radius)
                    ref.updateChildren(postValues)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun setFriendsOnTabItem(tab : TabLayout){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.getIdFromUser()}")
        var friends = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.child("status").value == "friend"){
                        friends++
                    }
                }
                tab.getTabAt(0)?.text = "${context.resources.getString(R.string.friend_friend)} (${friends.toString()})"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun setInvitationsOnTabItem(tab : TabLayout){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.getIdFromUser()}")
        var friends = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.child("status").value == "waiting"){
                        friends++
                    }
                }
                tab.getTabAt(1)?.text = "${context.resources.getString(R.string.friend_invitation)} (${friends.toString()})"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun acceptFriend(ctx: Context, userKey: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.getIdFromUser()}/$userKey")

        ref.child("status").setValue("friend")
        ref.child("keyChat").setValue(Utils().generatePassword(70))

        Toast.makeText(ctx, "You have accepted this user as friend :)", Toast.LENGTH_SHORT).show()
    }

    fun refuseFriend(ctx: Context, userKey: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.getIdFromUser()}/$userKey")
        ref.removeValue()
        Toast.makeText(ctx, "You have refused this user as friend :(", Toast.LENGTH_SHORT).show()
    }

    fun deleteFriend(ctx: Context, userKey: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.getIdFromUser()}/$userKey")
        ref.removeValue()
        Toast.makeText(ctx, "You have delete this user as friend :(", Toast.LENGTH_SHORT).show()
    }

    fun setEventsOnTabItem(tab : TabLayout){
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/eventsCreated")
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

    fun setEventsJoinedOnTabItem(tab : TabLayout){
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/eventsJoined")
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

    fun setNightMode(boolean: Boolean){
        FirebaseDatabase.getInstance().reference.child("parameters/${getIdFromUser()}/nightMode").setValue(boolean)
    }

    fun getNightMode(){
        FirebaseDatabase.getInstance().getReference("parameters/${getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("nightMode")){
                    if(p0.child("nightMode").value as Boolean){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

        })
    }
}