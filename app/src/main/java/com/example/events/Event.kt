package com.example.events

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import com.example.place.SessionGooglePlace
import com.example.project.EventInfoJojoActivity
import com.example.project.MainActivity
import com.example.project.PrivateUserActivity
import com.example.project.PublicUserActivity
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.user.PrivacyAccount
import com.example.user.User
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

/**
 * File created by Jonathan CHU on 17/07/19
 */
data class Event (
    val key : String = "",
    var name: String = "",
    var sport: Sport = Sport.INIT,
    var date: String = "",
    var time : String = "",
    var place: String = "",
    var nb_people: Int = 0,
    var description: String = "",
    var privacy : Privacy = Privacy.INIT,
    var creator : String = "",
    var participants : HashMap<String, String> = HashMap()
    ) {


    override fun toString(): String {
        return "${this.sport}   ${this.name}"
    }

    /**
     * insertEvent insert the current event in database
     */
    fun insertEvent(){
        //Add into events table
        val ref = FirebaseDatabase.getInstance().getReference("events/${this.key}")
        participants.put(creator, "confirmed")
        ref.setValue(this)
            .addOnSuccessListener {
                println("DATA INSERTED !!!!")
            }
        //Add into user createEvent
        val refUser = FirebaseDatabase.getInstance().getReference("users/$creator/eventsCreated/${this.key}")
        refUser.setValue(this.name)
            .addOnSuccessListener {
                println("DATA INSERTED !!!!")
            }
    }

    fun writeInfoEvent(context: Context, key: String?, textView: TextView, action: String) {
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    when (action) {
                        "name" -> textView.text = value.name
                        "creator" -> {
                            getCreator(context, key, value.creator, textView)
                            /*val user = SessionUser()
                            user.writeInfoUser(context, value.creator, textView, "identity")*/

                        }
                        "place" -> {
                            //INIT GOOGLE PLACE
                            //Init google place
                            val gg = SessionGooglePlace(context)
                            gg.init()
                            val placesClient = gg.createClient()

                            //Search place in according to the ID
                            val placeId : String = value.place
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
                        "date" -> textView.text = value.date
                        "time" -> textView.text = value.time
                        "nb_people" ->{
                            var count = 0
                            value.participants.forEach{
                                if(it.value == "confirmed"){
                                    count++
                                }
                            }
                            textView.text = "${count}  / ${value.nb_people.toString()}"
                        }
                        "description" -> textView.text = value.description
                        "sport" -> textView.text = value.sport.toString()
                        else -> textView.text = "NULL"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    /**
     * getCreator write the identity of the event's creator
     * @param key : the key of event
     * @param uid : the uid of event's creator
     * @param textView : the textView where we have to write information
     */
    fun getCreator(context: Context, key: String?, uid: String?, textView: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    if(SessionUser().getIdFromUser() != uid) {
                        var builder = StringBuilder()
                        builder.append(value.firstName).append(" ").append(value.name)
                        textView.text = builder


                        textView.setOnClickListener {
                            if (value.privacyAccount == PrivacyAccount.Public) {
                                val intent = Intent(context, PublicUserActivity::class.java)
                                intent.putExtra("user", value)
                                context.startActivity(intent)
                            }
                            else {
                                val intent = Intent(context, PrivateUserActivity::class.java)
                                intent.putExtra("user", value)
                                context.startActivity(intent)

                            }
                        }
                    }
                    else{
                        textView.text = "Me"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     * writeLogoSport show the logo of event's sport
     * @param context : the context
     * @param key : the event's key
     * @param imageView : the imageview where we have to show the logo
     */
    fun writeLogoSport(context: Context, key: String?, imageView : ImageView){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    imageView.setImageDrawable(context.getDrawable(getSport(value.sport.toString()).getLogo()))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    /**
     * deleteEvent delete the event in according to the event's key
     * @param context : the context use for change activity, make toast
     * @param key : the key of event wanted to be delete
     * @param session : the current session user
     */
    fun deleteEvent(context: Context, key : String?, session : SessionUser){
        val database = FirebaseDatabase.getInstance().reference
        database.child("events").child("$key").removeValue()
        database.child("users").child(session.getIdFromUser()).child("eventsCreated").child("$key").removeValue().addOnSuccessListener {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            EventInfoJojoActivity::finish
            context.startActivity(intent)
            Toast.makeText(context, "Event deleted successfully", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * participateEvent add participation of current user to an event
     * @param context : the context use for change activity, make toast
     * @param key : the key of event
     * @param session : the current session user wanted to be add
     */
    fun participateEvent(context: Context, key : String?, session : SessionUser){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/${session.getIdFromUser()}")
        ref.setValue("waiting").addOnSuccessListener {
            val intent = Intent(context, EventInfoJojoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("key", key)
            EventInfoJojoActivity::finish
            context.startActivity(intent)
            Toast.makeText(context, "Add your participation successfully. Wait your acceptation", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * cancelParticipation delete the participation of current user to an event
     * @param context : the context use for change activity, make toast
     * @param key : the key of event
     * @param session : the current session user
     */
    fun cancelParticipation(context: Context, key : String?, session : SessionUser){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/${session.getIdFromUser()}")
        ref.removeValue().addOnSuccessListener {
            val intent = Intent(context, EventInfoJojoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("key", key)
            EventInfoJojoActivity::finish
            context.startActivity(intent)
            Toast.makeText(context, "Delete your participation successfully", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * confirmParticipation confirm participation of user to an event
     * @param context : the context use for change activity, make toast
     * @param key : the key of event
     * @param user : the key which identify the user
     */
    fun confirmParticipation(context: Context, key: String?, user : String){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/$user")
        ref.setValue("confirmed")
        Toast.makeText(context, "Accept successfully", Toast.LENGTH_SHORT).show()
    }

    /**
     * refuseParticipation refuse participation of user to an event
     * @param context : the context use for change activity, make toast
     * @param key : the key of event
     * @param user : the key which identify the user
     */
    fun refuseParticipation(context: Context, key: String?, user : String){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/$user")
        ref.removeValue()
        //ref.setValue("confirmed")
        Toast.makeText(context, "You have refuse !", Toast.LENGTH_SHORT).show()
    }

    /**
     * deleteParticipation delete participation of user to an event
     * @param context : the context use for change activity, make toast
     * @param key : the key of event
     * @param user : the key which identify the user
     */
    fun deleteParticipation(context: Context, key: String?, user: String){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/$user")
        ref.removeValue()
        Toast.makeText(context, "You have delete this user !", Toast.LENGTH_SHORT).show()
    }

    fun getButton(context: Context, key: String?, button_edit : ImageButton, button_delete : ImageButton, button_participe: Button, button_cancel : Button){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    if(value.creator == SessionUser().getIdFromUser()){
                        button_edit.visibility = View.VISIBLE
                        button_delete.visibility = View.VISIBLE
                        button_participe.visibility = View.GONE
                    }
                    else{
                        if(value.participants.containsKey(SessionUser().getIdFromUser())){
                            button_participe.visibility = View.GONE
                            button_cancel.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    /**
     * getSport return an instance of sport in according to the sport choosen
     * @param sport : the name of sport
     */
    fun getSport(sport : String) : Sport {
        return when(sport){
            "FOOTBALL" -> Sport.FOOTBALL
            "BASKETBALL" -> Sport.BASKETBALL
            "GOLF" -> Sport.GOLF
            "HANDBALL" -> Sport.HANDBALL
            "MUSCULATION" -> Sport.MUSCULATION
            "TENNIS" -> Sport.TENNIS
            "TENNISDETABLE" -> Sport.TENNISDETABLE
            else -> {println(" SPORT !!! : ${sport}"); return Sport.INIT
            }
        }
    }

    /**
     * verifyCurrentUserIsCreator hide "see participants waiting" if current user isn't the creator of event
     * @param key : the key of event
     * @param textView : the textView
     * @param session : the session with uid current user
     */
    fun verifyCurrentUserIsCreator(key: String?, textView: TextView, session : SessionUser){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if(value?.creator != session.getIdFromUser()) {
                    textView.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     * verifyUserIsCreator hide the "Delete" button for the creator of event
     * @param key : the key of event
     * @param button : the button we want to hide
     * @param userKey : the key of user we want to check
     */
    fun verifyUserIsCreator(key : String, button: Button, userKey : String?){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                //Current user is the event's creator
                if(value?.creator == SessionUser().getIdFromUser()){
                    //User from item is not the creator
                    if(userKey != value?.creator){
                        button.visibility = View.VISIBLE
                    }
                    else{
                        button.visibility = View.GONE
                    }
                }
                else{
                    button.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     * showButtonIfCreator show button "Delete" if the current user is the creator of the event
     * @param key : the key of event
     * @param button : the button of item
     * @param session : the current session user
     */
    fun showButtonIfCreator(key: String?, button : Button, session : SessionUser){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if(value?.creator == session.getIdFromUser()) {
                    button.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
