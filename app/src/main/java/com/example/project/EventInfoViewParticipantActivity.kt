package com.example.project

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
    private lateinit var participeEventButton : Button
    private lateinit var cancelParticipationEventButton : Button
    private lateinit var waitingNumber : TextView

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

        val titleTextView : TextView = findViewById(R.id.title)
        val sportLogoImageView = findViewById<ImageView>(R.id.sport_logo)
        val creatorTextView = findViewById<TextView>(R.id.creator)
        val dateHour = findViewById<TextView>(R.id.date_hour)
        val freePlace = findViewById<TextView>(R.id.participants_number)
        val place = findViewById<TextView>(R.id.place)
        val participantsNumber = findViewById<TextView>(R.id.participant_number)
        waitingNumber = findViewById<TextView>(R.id.waiting_number)
        val noteTextView = findViewById<TextView>(R.id.note_event)
        val ratingBar  = findViewById<RatingBar>(R.id.ratingBar)

        participeEventButton = findViewById(R.id.button_participate)
        cancelParticipationEventButton = findViewById<Button>(R.id.button_cancel)
        val fullTextViex = findViewById<TextView>(R.id.full_event)
        participeEventButton.setOnClickListener(this)
        cancelParticipationEventButton.setOnClickListener(this)

        Event().getButton(this, keyEvent, participeEventButton, cancelParticipationEventButton, fullTextViex, noteTextView, ratingBar)

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
            "participant"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            waitingNumber,
            "waiting"
        ).toString()

        Event().writeLogoSport(this, keyEvent, sportLogoImageView)

        place.setOnClickListener(this)
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
