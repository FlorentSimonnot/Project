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

    private val session = SessionUser(this)
    private var mMap: GoogleMap? = null
    private lateinit var keyEvent : String

    /*------------TextView, EditText etc...-----------*/
    private lateinit var titleTextView : TextView
    private lateinit var sportLogoImageView : ImageView
    private lateinit var waitingNumber : TextView
    private lateinit var creatorTextView : TextView
    private lateinit var dateHour : TextView
    private lateinit var freePlace : TextView
    private lateinit var place : TextView
    private lateinit var participantsNumber : TextView
    private lateinit var description : TextView

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

        titleTextView = findViewById(R.id.title)
        sportLogoImageView = findViewById(R.id.sport_logo)
        creatorTextView = findViewById(R.id.creator)
        dateHour = findViewById(R.id.date_hour)
        freePlace = findViewById(R.id.participants_number)
        place = findViewById(R.id.place)
        participantsNumber = findViewById(R.id.participant_number)
        waitingNumber = findViewById(R.id.waiting_number)
        description = findViewById(R.id.description)

        val deleteEventButton = findViewById<LinearLayout>(R.id.delete)
        val editEventButton = findViewById<LinearLayout>(R.id.edit)

        val participantsButton = findViewById<LinearLayout>(R.id.participants)
        val waitingButton = findViewById<LinearLayout>(R.id.waiting)
        val accessibility = findViewById<LinearLayout>(R.id.accessibility)

        participantsButton.setOnClickListener(this)
        waitingButton.setOnClickListener(this)
        editEventButton.setOnClickListener(this)
        deleteEventButton.setOnClickListener(this)
        accessibility.setOnClickListener(this)


        /*-------------------------------Show info---------------------------------*/
        FirebaseDatabase.getInstance().getReference("events/$keyEvent").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                writeEventInfo(this@EventInfoJojoActivity)
            }

        })



    }

    private fun writeEventInfo(context: Context){
        val event = Event()

        //event.getButton(this, keyEvent, participeEventButton, cancelParticipationEventButton, fullTextView, noteTextView, ratingBar)

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

        event.writeInfoEvent(
            this,
            keyEvent,
            description,
            "description"
        ).toString()

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
            R.id.participants ->{
                val intent = Intent(this, ParticipantsActivity::class.java)
                intent.putExtra("key", keyEvent)
                startActivity(intent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.waiting ->{
                val intent = Intent(this, ParticipantsWaitedActivity::class.java)
                intent.putExtra("key", keyEvent)
                startActivity(intent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.delete ->{
                val deleteEventAlertDialog = AlertDialog.Builder(this)
                deleteEventAlertDialog.setTitle("Are you sure?")
                deleteEventAlertDialog.setMessage("Your event will be deleted.\nConfirm?")
                deleteEventAlertDialog.setPositiveButton("Yes"){_, _ ->
                    Event().deleteEvent(this, keyEvent, session)
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                deleteEventAlertDialog.setNegativeButton("No"){_, _ ->

                }
                deleteEventAlertDialog.show()
            }
            R.id.edit ->{
                val intent = Intent(this, ModifyEventActivity::class.java)
                intent.putExtra("key", keyEvent)
                startActivity(intent)
            }
            R.id.accessibility -> {
                val intent = Intent(this, AddPeopleToEventActivity::class.java)
                intent.putExtra("key", keyEvent)
                startActivity(intent)
            }
            else ->{
                //Nothing
            }
        }
    }
}
