package com.example.session

import androidx.appcompat.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.renderscript.Sampler
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.example.events.Distance
import com.example.login.EmailLogin
import com.example.place.SessionGooglePlace
import com.example.project.LoginActivity
import com.example.project.R
import com.example.project.SettingsActivity
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

    /**
     *  isLogin()
     *  isLogin verify if a user is connected
     *  @return true if user is connected. False else
     *  @author Florent SIMONNOT
     */
    fun isLogin() : Boolean{
        return FirebaseAuth.getInstance().uid != null
    }

    /**
     *  signOut()
     *  signOut close the current login session.
     *  @author Florent SIMONNOT
     */
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    /*
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
    }*/

    /**
     * getIdFromUser()
     * getIdFromUser return the id from current user
     * @return String which is the id of user
     * @exception \throw exception if any user is connected
     * @author Florent SIMONNOT
     */
    fun getIdFromUser() : String{
        if(user == null){
            context.startActivity(Intent(context, LoginActivity::class.java))
            return ""
        }
        return user!!.uid
    }

    /**
     *
     * login(email: String, password : String, context: Context)
     * log a user with an email and password
     * @param email - the email of user
     * @param password - the password of user
     * @param context - the context of application
     * @exception \throw exception if a user is already connected
     * @author Florent SIMONNOT
     *
     */
    fun login(email: String, password : String, context: Context){
        if(user != null){
            throw Exception("Impossible")
        }
        else{
            val emailLogin = EmailLogin(context, email, password)
            emailLogin.login(context)
        }
    }

    /**
     * deleteUser(context: Context)
     * deleteUser delete the current user from database
     * @param context for intent
     * @author Florent SIMONNOT
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

    /**
     * resetPassword(context: Context, auth : FirebaseAuth, emailAddress : String)
     * resetPassword send an email to reset password.
     * @param context - The context of application
     * @param auth - A FirebaseAuth
     * @param emailAddress - the address mail of user we want change his password
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

    /**
     * writeNameUserDiscussion(context: Context, uid: String?, supportActionBar: ActionBar)
     * write the user's name in a discussion
     * @param context - the context of application
     * @param uid - the id of user
     * @param supportActionBar - the toolbar
     * @author Florent SIMONNOT.
     */
    fun writeNameUserDiscussion(context: Context, uid: String?, supportActionBar: ActionBar){
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    supportActionBar.title = "${value.firstName} ${value.name}"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    fun writeRadius(context: Context, uid: String?, seekBar: ProgressBar, textView: TextView){
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("radius")) {
                    val arround = dataSnapshot.child("radius").value as Long
                    seekBar.progress = 100/25*arround.toInt()
                    writeKmOrMiles(Distance(arround.toDouble()), textView, false)
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
                        "sex" -> textView.text = value.sex.getString(context)
                        "birthday" -> textView.text = value.birthday
                        "describe" -> textView.text = value.description
                        "deleteFriend" -> textView.text = "Supprimer ${value.firstName} ${value.name} de ses amis"
                        "sendMessage" -> textView.text = "Envoyé un message à ${value.firstName} ${value.name}"
                        "seeProfile" -> textView.text = "Voir le profil de ${value.firstName} ${value.name}"
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

    fun writeKmOrMiles(distance : Distance, textView: TextView, type : Boolean = true){
        FirebaseDatabase.getInstance().getReference("parameters/${getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("measuringSystem")){
                    if(p0.child("measuringSystem").value as String == "Km"){
                        textView.text = if(!type){"${String.format("%.2f", distance.getDistance(false))} Km" }else{"${distance.getDistanceLong(false)} Km"}
                    }
                    else{
                        textView.text = if(!type){"${String.format("%.2f", distance.getDistance(true))} Mi" }else{"${distance.getDistanceLong(true)} Mi"}
                    }
                }
                else{
                    textView.text = if(!type){"${String.format("%.2f", distance.getDistance(false))} Km" }else{"${distance.getDistanceLong(false)} Km"}
                }
            }

        })
    }

    /**
     * showPhotoUser(context: Context, imageView: ImageView)
     * Show the photo of current user in an imageView.
     * @param context - the context of application
     * @param imageView - the imageView where set the image
     * @author Florent SIMONNOT.
     */
    fun showPhotoUser(context: Context, imageView: ImageView) {
        User().showPhotoUser(context, imageView, this.getIdFromUser())
    }

    fun setButtonFriend(buttonAddFriend : Button, buttonRemoveFriend : Button, buttonCancelFriend : Button, buttonAcceptFriend : Button , uid: String){
        val ref = FirebaseDatabase.getInstance().getReference("friends/$uid/${getIdFromUser()}")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                buttonAddFriend.visibility = View.VISIBLE
                buttonCancelFriend.visibility = View.GONE
                buttonRemoveFriend.visibility = View.GONE
                buttonAcceptFriend.visibility = View.GONE

                when(p0.child("status").value) {
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
        })
    }

    fun setButtonAcceptFriend(uid : String, buttonAcceptFriend: Button, buttonAddFriend: Button){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.getIdFromUser()}/$uid")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                println("YES ! ${p0.child("status").value}")
                if(p0.child("status").value == "waiting"){
                    buttonAcceptFriend.visibility = View.VISIBLE
                    buttonAddFriend.visibility = View.GONE
                }
            }

        })
    }

    /*
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
    }*/

    /**
     *  setNewPassword(oldPassword: String, newPassword: String)
     *  Change the current password for a new password.
     *  @param oldPassword - the former password
     *  @param newPassword - the new password
     *  @author Florent SIMONNOT
     */
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

    /**
     * setEventsOnTabItem(tab : TabLayout)
     * Set the title of the tabLayout. We write the number of events created by the current user.
     * @param tab - The Tablayout
     * @author Florent SIMONNOT
     */
    fun setEventsOnTabItem(tab : TabLayout){
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/eventsCreated")
        //var events = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                /*data.forEach {
                    events++
                }*/
                tab.getTabAt(0)?.text = "${context.resources.getString(R.string.events_created)} (${data.count()})"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     * setEventsJoinedOnTabItem(tab : TabLayout)
     * Set the title of the tabLayout. We write the number of events joined by the current user.
     * @param tab - The Tablayout
     * @author Florent SIMONNOT
     */
    fun setEventsJoinedOnTabItem(tab : TabLayout){
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.getIdFromUser()}/eventsJoined")
        //var events = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                /*data.forEach {
                    events++
                }*/
                tab.getTabAt(1)?.text = "${context.resources.getString(R.string.events_joined)} (${data.count()})"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     * setNightMode()
     * Change parameter of dark theme in database. Set to true activate if the value is false. Else set deactivate.
     * @author Florent SIMONNOT
     */
    fun setNightMode(){

        val sharedPref = SharedPref(context.applicationContext)
        sharedPref.setNightModeState(!(sharedPref.loadNightModeState()!!))
        getNightMode()

        /*FirebaseDatabase.getInstance().getReference("parameters/${getIdFromUser()}").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("nightMode")){
                    if(p0.child("nightMode").value as Boolean){
                        FirebaseDatabase.getInstance().reference.child("parameters/${getIdFromUser()}/nightMode").setValue(false)
                    }
                    else{
                        FirebaseDatabase.getInstance().reference.child("parameters/${getIdFromUser()}/nightMode").setValue(true)
                    }
                }
                else{
                    FirebaseDatabase.getInstance().reference.child("parameters/${getIdFromUser()}/nightMode").setValue(true)
                }
            }

        })*/
    }

    /**
     * getNightMode()
     * Change dark mode in application if user want to use dark mode
     * @author Florent SIMONNOT.
     */
    fun getNightMode(){

        if(SharedPref(context.applicationContext).loadNightModeState()!!){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        /*FirebaseDatabase.getInstance().getReference("parameters/${getIdFromUser()}").addValueEventListener(object : ValueEventListener{
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

        })*/
    }

    /**
     * setMeasuringSystem(value : String)
     * Change the measuring system value in database with the new value from parameter
     * @param value - the new value of measuring system
     */
    fun setMeasuringSystem(value : String){
        FirebaseDatabase.getInstance().reference.child("parameters/${getIdFromUser()}/measuringSystem").setValue(value)
    }
}