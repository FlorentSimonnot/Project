package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.dialog.AlertDialogCustom
import com.example.events.Event
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import java.util.*
import com.google.android.libraries.places.api.Places
import org.w3c.dom.Text


class EventInfoJojoActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private val session = SessionUser()
    private var mMap: GoogleMap? = null
    private lateinit var keyEvent : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_jojo)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        keyEvent = infos?.getString("key").toString()

        val titleTextView : TextView = findViewById(R.id.title)
        val sportLogoImageView = findViewById<ImageView>(R.id.sport_logo)
        val creatorTextView = findViewById<TextView>(R.id.creator)
        val dateHour = findViewById<TextView>(R.id.date_hour)
        val freePlace = findViewById<TextView>(R.id.participants_number)
        val place = findViewById<TextView>(R.id.place)
        val participantsNumber = findViewById<TextView>(R.id.participant_number)
        val waitingNumber = findViewById<TextView>(R.id.waiting_number)

        val deleteEventButton = findViewById<LinearLayout>(R.id.delete)
        val editEventButton = findViewById<LinearLayout>(R.id.edit)

        val participantsButton = findViewById<LinearLayout>(R.id.participants)
        val waitingButton = findViewById<LinearLayout>(R.id.waiting)
        val refuseButton = findViewById<LinearLayout>(R.id.refuse)

        participantsButton.setOnClickListener(this)
        waitingButton.setOnClickListener(this)
        refuseButton.setOnClickListener(this)
        editEventButton.setOnClickListener(this)
        deleteEventButton.setOnClickListener(this)
        //participeEventButton.setOnClickListener(this)
        //cancelParticipationEventButton.setOnClickListener(this)

        /*val placeTextView = findViewById<TextView>(R.id.place)
        val dateTextView = findViewById<TextView>(R.id.date)
        val timeTextView = findViewById<TextView>(R.id.time)
        val nbPeopleTextView = findViewById<TextView>(R.id.nb_people)
        val descriptionTextView = findViewById<TextView>(R.id.description)
        val button_delete = findViewById<ImageButton>(R.id.button_delete)
        val button_edit = findViewById<ImageButton>(R.id.button_edit)
        val button_participate = findViewById<Button>(R.id.button_participate)
        val button_cancel = findViewById<Button>(R.id.button_cancel)
        val participantsWaiting = findViewById<TextView>(R.id.participants_waiting)
        val participantsConfirmed = findViewById<TextView>(R.id.participants)

        Event().verifyCurrentUserIsCreator(keyEvent, participantsWaiting, session)


        Event().writeInfoEvent(
            this,
            keyEvent,
            titleTextView,
            "name"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            sportTextView,
            "sport"
        )

        Event().writeLogoSport(this, keyEvent, sportLogoImageView)

        Event().writeInfoEvent(
            this,
            keyEvent,
            creatorTextView,
            "creator"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            placeTextView,
            "place"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            dateTextView,
            "date"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            timeTextView,
            "time"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            nbPeopleTextView,
            "nb_people"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            descriptionTextView,
            "description"
        )


        Event().getButton(this, keyEvent, button_edit, button_delete, button_participate, button_cancel)

        button_edit.setOnClickListener {

        }

        button_delete.setOnClickListener {
            val deleteEventAlertDialog = AlertDialog.Builder(this)
            deleteEventAlertDialog.setTitle("Are you sure?")
            deleteEventAlertDialog.setMessage("Your event will be deleted.\nConfirm?")
            deleteEventAlertDialog.setPositiveButton("Yes"){_, _ ->
                Event().deleteEvent(this, keyEvent, session)
                startActivity(Intent(this, MainActivity::class.java))
            }
            deleteEventAlertDialog.setNegativeButton("No"){_, _ ->

            }
            deleteEventAlertDialog.show()
        }

        button_participate.setOnClickListener {
            Event().participateEvent(this, keyEvent, session)
        }

        button_cancel.setOnClickListener {
            Event().cancelParticipation(this, keyEvent, session)
        }

        */

        Event().writeInfoEvent(
            this,
            keyEvent,
            titleTextView,
            "name"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            creatorTextView,
            "creator"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            dateHour,
            "dateAndTime"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            freePlace,
            "freePlace"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            place,
            "place"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            participantsNumber,
            "participe"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            waitingNumber,
            "waiting"
        ).toString()

        Event().writeLogoSport(this, keyEvent, sportLogoImageView)



    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        EventInfoJojoActivity::finish
        startActivity(intent)
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val context = this

        val ref = FirebaseDatabase.getInstance().getReference("events/$keyEvent")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Event::class.java)
                if (value != null) {
                    //INIT GOOGLE PLACE
                    //Init google place
                    val gg = SessionGooglePlace(context)
                    gg.init()
                    val placesClient = gg.createClient()

                    //Search place in according to the ID
                    val placeId : String = value.place
                    val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG)
                    val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)


                    placesClient.fetchPlace(request)
                        .addOnSuccessListener {
                            val place : Place = it.place
                            val coords : LatLng? = place.latLng
                            if(coords != null){
                                mMap?.addMarker(MarkerOptions().position(coords).title(place.name))
                                mMap?.moveCamera(CameraUpdateFactory.newLatLng(coords))
                                mMap?.setMinZoomPreference(11F)
                            }
                        }
                        .addOnFailureListener {
                            //textView1.text = it.message
                        }
                }
            }
        })

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.participants ->{
                val intent = Intent(this, ParticipantsActivity::class.java)
                intent.putExtra("key", keyEvent)
                finish()
                startActivity(intent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.waiting ->{
                val intent = Intent(this, ParticipantsWaitedActivity::class.java)
                intent.putExtra("key", keyEvent)
                finish()
                startActivity(intent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.refuse ->{

            }
            R.id.delete ->{
                val deleteEventAlertDialog = AlertDialog.Builder(this)
                deleteEventAlertDialog.setTitle("Are you sure?")
                deleteEventAlertDialog.setMessage("Your event will be deleted.\nConfirm?")
                deleteEventAlertDialog.setPositiveButton("Yes"){_, _ ->
                    Event().deleteEvent(this, keyEvent, session)
                    startActivity(Intent(this, MainActivity::class.java))
                }
                deleteEventAlertDialog.setNegativeButton("No"){_, _ ->

                }
                deleteEventAlertDialog.show()
            }
            R.id.edit ->{

            }
            else ->{
                //Nothing
            }
        }
    }
}
