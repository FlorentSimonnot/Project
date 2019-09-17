package com.example.events

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.*
import com.example.dateCustom.DateUTC
import com.example.notification.Action
import com.example.notification.CloudFunction
import com.example.place.SessionGooglePlace
import com.example.project.*
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.status.Status
import com.example.user.Gender
import com.example.user.PrivacyAccount
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.gms.maps.GoogleMap
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
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
    var date : Long = 0,
    var place: PlaceEvent = PlaceEvent(""),
    var nb_people: Int = 0,
    var description: String = "",
    var privacy : Privacy = Privacy.INIT,
    var creator : String = "",
    var participants : HashMap<String, Status> = HashMap(),
    var finish : Boolean = false,
    var note : Double = 0.0
    ) : Serializable {

    override fun toString(): String {
        return "${this.sport}   ${this.name}"
    }

    /**
     * insertEvent insert the current event in database
     */
    fun insertEvent(context: Context){
        //Add into events table
        val ref = FirebaseDatabase.getInstance().getReference("events/${this.key}")
        ref.setValue(this)
            .addOnSuccessListener {
                val refU = FirebaseDatabase.getInstance().getReference("events/$key/participants/${SessionUser(context).getIdFromUser()}")
                refU.child("status").setValue("creator").addOnSuccessListener {
                    //Ok
                }
            }
    }

    fun goPlaceWithWaze(context: Context, key : String?){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /*val value = dataSnapshot.getValue(Event::class.java)
                if(value != null){
                    //INIT GOOGLE PLACE
                    val gg = SessionGooglePlace(context)
                    gg.init()
                    val placesClient = gg.createClient()

                    //Search place in according to the ID
                    val placeId : String = value.place
                    val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS)
                    val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)


                    placesClient.fetchPlace(request)
                        .addOnSuccessListener {
                            val place : Place = it.place
                            try {
                                val url = "https://waze.com/ul?q=${place.name}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }catch (exception : ActivityNotFoundException){
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"))
                                context.startActivity(intent)
                            }

                        }
                        .addOnFailureListener {
                            //Error
                        }
                }*/
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun writeInfoEvent(context: Context, key: String?, textView: TextView, action: String) {
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    when (action) {
                        "name" -> {
                            textView.text = value.name.toString()
                        }
                        "creator" -> {
                            getCreator(context, key, value.creator, textView)
                        }
                        "place" -> {
                            val placeEvent = PlaceEvent(
                                dataSnapshot.child("place").child("idPlace").value as String,
                                dataSnapshot.child("place").child("address").value as String)
                            textView.text = value.place.address
                        }
                        "numberOfParticipants" -> {
                            textView.text = "${value.nb_people}"
                        }
                        "date" -> {
                            textView.text = DateUTC(value.date).showDate()
                        }
                        "dateAndTime" ->{
                            textView.text = "${DateUTC(value.date).showDate()} at ${DateUTC(value.date).showTime()}"
                        }
                        "time" -> {
                            textView.text = "${DateUTC(value.date).showTime()}"
                        }
                        "participant" -> {
                            var res = 0
                            val data = value.participants
                            data.forEach {
                                if(it.value.status == "confirmed"){
                                    res++
                                }
                            }
                            res++
                            textView.text ="$res"
                        }
                        "waiting" -> {
                            var res = 0
                            val data = value.participants
                            data.forEach {
                                if(it.value.status == "waiting"){
                                    res++
                                }
                            }
                            textView.text = "$res"
                        }
                        "freePlace"  -> {
                            val number = value.nb_people - value.participants.size
                            textView.text = if(number < 2){
                                "$number " + context.getString(R.string.event_info_place)
                            }else{
                                "$number " + context.getString(R.string.event_info_places)
                            }

                        }
                        "description" -> textView.text = value.description
                        "sport" -> {
                            textView.text = value.sport.getNameSport(context)
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, value.sport!!.getLogoSport(), 0)
                            textView.compoundDrawablePadding = 20
                        }
                        "privacy" -> textView.text = "${value.privacy.toString().capitalize()}"
                        else -> textView.text = "NULL"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun showPhotoCreatorEvent(context: Context, key: String?, imageView: ImageView) {
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    getCreatorPhoto(context, key, value.creator, imageView)
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
                //val value = dataSnapshot.getValue(User::class.java)
                val firstName: String = dataSnapshot.child("firstName").value as String
                val name: String = dataSnapshot.child("name").value as String
                val accountPrivacy = dataSnapshot.child("privacyAccount").value as String
                if (firstName != null) {
                    if(SessionUser(context).getIdFromUser() != uid) {
                        val builder = StringBuilder(context.getString(R.string.event_info_creator))
                        builder.append(firstName).append(" ").append(name)
                        textView.text = builder


                        textView.setOnClickListener {
                            if (PrivacyAccount.valueOf(accountPrivacy) == PrivacyAccount.Public) {
                                val intent = Intent(context, PublicUserActivity::class.java)
                                intent.putExtra("user", uid)
                                context.startActivity(intent)
                            }
                            else {
                                val intent = Intent(context, PrivateUserActivity::class.java)
                                intent.putExtra("user", uid)
                                context.startActivity(intent)

                            }
                        }
                    }
                    else{
                        textView.text = context.getString(R.string.event_info_creator_you)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getCreatorPhoto(context: Context, key: String?, uid: String?, imageView: ImageView) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val urlPhoto = dataSnapshot.child("urlPhoto").value as String
                if(urlPhoto.isNotEmpty()){

                }
                else{
                    imageView.setImageDrawable(context.resources.getDrawable(R.drawable.ic_boy))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun hideMore(context: Context, key: String?, textView: TextView, imageButton: ImageButton, userKey: String?){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                //Current user is the event's creator
                if(value?.creator == userKey || userKey == SessionUser(context).getIdFromUser()){
                    imageButton.visibility = View.GONE
                    textView.text = "${context.resources.getString(R.string.you)}"
                }
                else{
                    imageButton.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun writeNotificationEvent(context: Context, key: String?, textView: TextView, action : Action){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    //textView.text =
                    /*when (action) {
                        Action.ACCEPT -> {
                            //val s = "Your participation to <b> ${value.name} </b> has been accepted !"
                            //textView.text = Html.fromHtml(s)
                        }
                        else -> "NULL"
                    }*/
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
    fun writeLogoSport(context: Context, key: String?, imageView : ImageView, size : Int = 24){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    val sport = value.sport
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageDrawable(context.getDrawable(sport.getLogoSport(size)))
                    }
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
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.removeValue()
        val intent = Intent(context,HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        EventInfoJojoActivity::finish
        context.startActivity(intent)
    }

    /**
     * participateEvent add participation of current user to an event
     * @param context : the context use for change activity, make toast
     * @param key : the key of event
     * @param session : the current session user wanted to be add
     */
    fun participateEvent(context: Context, key : String?, session : SessionUser, buttonToHide : Button, buttonToShow : Button, textView: TextView){
        /* Add in event participants */
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/${session.getIdFromUser()}")
        ref.child("status").setValue("waiting").addOnSuccessListener {
            buttonToHide.visibility = View.GONE
            buttonToShow.visibility = View.VISIBLE
            textView.text = "${textView.text.toString().toInt()+1}"
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
        ref.child("status").setValue("confirmed")
        Toast.makeText(context, "Accept successfully", Toast.LENGTH_SHORT).show()
    }

    fun acceptInvitation(context: Context, key: String?, user : String){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/$user")
        ref.child("status").setValue("accepted")
        Toast.makeText(context, "Welcome !", Toast.LENGTH_SHORT).show()
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

    fun inviteFriend(context: Context, key: String?, user: String){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/$user")
        ref.child("status").setValue("invitation")
        Toast.makeText(context, context.getString(R.string.event_info_invite_toast), Toast.LENGTH_SHORT).show()
    }

    fun deleteInvitationEvent(context: Context, key: String?, user: String){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants/$user")
        ref.removeValue()
        Toast.makeText(context, context.getString(R.string.event_info_cancel_invitation_toast), Toast.LENGTH_SHORT).show()
    }

    /**
     * getButton look if the current already participate to the event or is invited. Show and the buttons in according to the status of participation.
     * @param context : the current context
     * @param button_participe : the button for participation
     * @param button_cancel : the button for cancel his participation
     *
     */
    fun getButton(context: Context, key: String?,
                  button_participe: Button,
                  button_cancel : Button,
                  buttonAcceptInvitation : Button,
                  buttonRefuseInvitaton : Button,
                  textView : TextView,
                  textViewNote : TextView,
                  ratingBar: RatingBar
                  ){
        var isInvited = false
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                dataSnapshot.child("participants").children.forEach {
                    if (it.key == SessionUser(context).getIdFromUser() && it.child("status").value == "invitation") {
                        isInvited = true
                    }
                }
                if (value != null) {
                    if(!value.finish) {
                        if(isInvited){
                            button_cancel.visibility = View.GONE
                            button_participe.visibility = View.GONE
                            buttonAcceptInvitation.visibility = View.VISIBLE
                            buttonRefuseInvitaton.visibility = View.VISIBLE
                        }
                        else{
                            buttonAcceptInvitation.visibility = View.GONE
                            buttonRefuseInvitaton.visibility = View.GONE
                            if (value.participants.size < value.nb_people) {
                                if (value.participants.contains(SessionUser(context).getIdFromUser())) {
                                    button_participe.visibility = View.GONE
                                    button_cancel.visibility = View.VISIBLE
                                } else {
                                    button_participe.visibility = View.VISIBLE
                                    button_cancel.visibility = View.GONE
                                }
                            } else {
                                if (value.participants.contains(SessionUser(context).getIdFromUser())) {
                                    button_participe.visibility = View.GONE
                                    button_cancel.visibility = View.VISIBLE
                                } else {
                                    button_participe.visibility = View.GONE
                                    button_cancel.visibility = View.GONE
                                    textView.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                    else{
                        val ref = FirebaseDatabase.getInstance().getReference("users/${SessionUser(context).getIdFromUser()}/eventsJoined/")
                        ref.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                //No
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                ratingBar.visibility = View.VISIBLE
                                textViewNote.visibility = View.VISIBLE
                            }
                        })
                    }
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
    fun verifyUserIsCreator(context: Context, key : String?, button: ImageButton, buttonMessage : ImageButton, userKey : String?){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                //Current user is the event's creator
                if(value?.creator == SessionUser(context).getIdFromUser()){
                    //User from item is not the creator
                    if(userKey != value?.creator){
                        button.visibility = View.VISIBLE
                        buttonMessage.visibility = View.VISIBLE
                    }
                    else{
                        button.visibility = View.GONE
                        buttonMessage.visibility = View.GONE
                    }
                }
                else{
                    button.visibility = View.GONE
                    buttonMessage.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun updateEvent(context: Context) {
        val event = this
        val ref = FirebaseDatabase.getInstance().getReference("events/$key")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if(value != null){
                    val participants = value.participants
                    event.participants = participants
                    if(place.idPlace.isEmpty()){
                        place = value.place
                    }
                    ref.setValue(event).addOnSuccessListener {
                        Toast.makeText(context, "Edit event successfully !", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, EventInfoJojoActivity::class.java)
                        intent.putExtra("key", key)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        ModifyEventActivity::finish
                        context.startActivity(intent)
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error ${it.message} !", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCanceledListener {
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
