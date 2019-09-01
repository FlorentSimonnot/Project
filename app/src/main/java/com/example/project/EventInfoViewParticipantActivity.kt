package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.example.dialog.AlertDialogCustom
import com.example.events.Event
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class EventInfoViewParticipantActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private val session = SessionUser(this)
    private var mMap: GoogleMap? = null

    private lateinit var keyEvent : String

    /*------------TextView, EditText etc...-----------*/
    private lateinit var titleTextView : TextView
    private lateinit var sportLogoImageView : ImageView
    private lateinit var participeEventButton : Button
    private lateinit var cancelParticipationEventButton : Button
    private lateinit var waitingNumber : TextView
    private lateinit var creatorTextView : TextView
    private lateinit var dateHour : TextView
    private lateinit var freePlace : TextView
    private lateinit var place : TextView
    private lateinit var participantsNumber : TextView
    private lateinit var noteTextView : TextView
    private lateinit var ratingBar : RatingBar
    private lateinit var fullTextView : TextView
    private lateinit var description : TextView

     /*-----------------------------------------------*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_view_participant)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        keyEvent = infos?.getString("key").toString()

        /*--------------------------------Init--------------------------------------*/
        titleTextView = findViewById(R.id.title)
        sportLogoImageView = findViewById(R.id.sport_logo)
        creatorTextView = findViewById(R.id.creator)
        dateHour = findViewById(R.id.date_hour)
        freePlace = findViewById(R.id.participants_number)
        place = findViewById(R.id.place)
        participantsNumber = findViewById(R.id.participant_number)
        waitingNumber = findViewById(R.id.waiting_number)
        noteTextView = findViewById(R.id.note_event)
        ratingBar  = findViewById(R.id.ratingBar)
        participeEventButton = findViewById(R.id.button_participate)
        cancelParticipationEventButton = findViewById(R.id.button_cancel)
        fullTextView = findViewById(R.id.full_event)
        description = findViewById(R.id.description)

        /*--------------------------------Click------------------------------------*/
        participeEventButton.setOnClickListener(this)
        cancelParticipationEventButton.setOnClickListener(this)
        place.setOnClickListener(this)


        /*-------------------------------Show info---------------------------------*/
        FirebaseDatabase.getInstance().getReference("events/$keyEvent").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                writeEventInfo(this@EventInfoViewParticipantActivity)
            }

        })

    }

    private fun writeEventInfo(context: Context){
        val event = Event()

        event.getButton(this, keyEvent, participeEventButton, cancelParticipationEventButton, fullTextView, noteTextView, ratingBar)

        event.writeInfoEvent(
            this,
            keyEvent,
            titleTextView,
            "name"
        ).toString()

        event.writeInfoEvent(
            this,
            keyEvent,
            creatorTextView,
            "creator"
        ).toString()

        event.writeInfoEvent(
            this,
            keyEvent,
            dateHour,
            "dateAndTime"
        ).toString()

        event.writeInfoEvent(
            this,
            keyEvent,
            freePlace,
            "freePlace"
        ).toString()

        event.writeInfoEvent(
            this,
            keyEvent,
            place,
            "place"
        ).toString()

        event.writeInfoEvent(
            this,
            keyEvent,
            participantsNumber,
            "participant"
        ).toString()

        event.writeInfoEvent(
            this,
            keyEvent,
            waitingNumber,
            "waiting"
        ).toString()

        event.writeInfoEvent(this, keyEvent, description, "description")

        event.writeLogoSport(this, keyEvent, sportLogoImageView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
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
            R.id.button_participate -> {
                Event().participateEvent(this, keyEvent, session, participeEventButton, cancelParticipationEventButton, waitingNumber)
            }
            R.id.button_cancel -> {
                Event().cancelParticipation(this, keyEvent, session, cancelParticipationEventButton, participeEventButton, waitingNumber)
            }
            R.id.place -> {
                Event().goPlaceWithWaze(this@EventInfoViewParticipantActivity, keyEvent)
            }
        }
    }
}
