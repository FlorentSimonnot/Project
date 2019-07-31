package com.example.events

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.*
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.user.User
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode
import org.w3c.dom.Text
import java.lang.StringBuilder
import java.util.*

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
    var creator : String = ""
    ) {


    override fun toString(): String {
        return "${this.sport}   ${this.name}"
    }

    /**insertEvent insert data from event into database
     *
     */
    fun insertEvent(){
        //Add into events table
        val ref = FirebaseDatabase.getInstance().getReference("events/${this.key}")
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
                            getCreator(key, value.creator, textView)
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
                        "nb_people" -> textView.text = value.nb_people.toString()
                        "description" -> textView.text = value.description
                        "sport" -> textView.text = value.sport.toString()
                        else -> textView.text = "NULL"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }


    fun getCreator(key: String?, uid: String?, textView: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    if(SessionUser().getIdFromUser() != uid) {
                        var builder = StringBuilder()
                        builder.append(value.firstName).append(" ").append(value.name)
                        textView.text = builder
                    }
                    else{
                        textView.text = "Me"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

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

    fun getButton(context: Context, key: String?, button_edit : ImageButton, button_delete : ImageButton, button: Button){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    if(value.creator == SessionUser().getIdFromUser()){
                        button_edit.visibility = View.VISIBLE
                        button_delete.visibility = View.VISIBLE
                        button.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

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


}
