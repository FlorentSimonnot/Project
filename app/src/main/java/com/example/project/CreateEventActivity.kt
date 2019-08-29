package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.events.Privacy
import com.example.sport.Sport
import com.example.picker.TimePicker
import com.example.picker.DatePicker
import android.widget.TextView
import java.text.DateFormat
import java.util.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import android.annotation.SuppressLint
import android.app.*
import com.example.events.Event
import com.example.events.EventFirstStep
import com.example.form.FormCreateEvent
import com.example.picker.NumberPickerCustom
import com.example.picker.StringPickerCustom
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.example.utils.Utils
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_create_event.*


class CreateEventActivity : AppCompatActivity(),
    TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener,
    NumberPicker.OnValueChangeListener  {

    private var event = EventFirstStep()

    private lateinit var name : EditText
    private lateinit var date : TextView
    private lateinit var time : TextView
    private lateinit var autoCompleteSport : TextView
    private lateinit var place : TextView
    private lateinit var peopleNumber : TextView
    private lateinit var description : EditText
    private lateinit var privacy : TextView
    private lateinit var error : TextView
    private lateinit var stringPrivacy : Array<String>
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private val apiKey = "AIzaSyAOWv25i-loOcpNvUqCvJ9oe7LkVp8FGrw"
    private var placeId : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Create your event"

        name = findViewById(R.id.name_event)
        autoCompleteSport = findViewById(R.id.sport)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        place = findViewById(R.id.place)
        peopleNumber = findViewById(R.id.numberPeople)
        description = findViewById(R.id.description)
        privacy = findViewById(R.id.privacy_event)
        error = findViewById(R.id.error)

        stringPrivacy = arrayOf("Public", "Private", "Only invitation")

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }


        if(intent.hasExtra("event")){
            event = intent.getSerializableExtra("event") as EventFirstStep
            initializeFields()
        }
        else{
            initializeFields()
        }

        time.setOnClickListener {
            val timePicker = TimePicker()
            timePicker.show(supportFragmentManager, "Choose time")
        }

        date.setOnClickListener {
            val datePicker = DatePicker()
            datePicker.show(supportFragmentManager, "Choose date")
        }

        autoCompleteSport.setOnClickListener{
            val intent = Intent(this, SportActivity::class.java)
            event.name = name.text.toString()
            event.description = description.text.toString()
            event.place = placeId!!
            intent.putExtra("event", event)
            startActivity(intent)
        }

        numberPeople.setOnClickListener {
            val numberPicker = NumberPickerCustom(1, 22, "Choose max number people", "Select a value")
            numberPicker.setValueChangeListener(this)
            numberPicker.show(supportFragmentManager, "People picker")
        }

        privacy.setOnClickListener {
            val numberPicker = StringPickerCustom(0, 2, "Choose max number people", "Select a value", stringPrivacy)
            numberPicker.setValueChangeListener(this)
            numberPicker.show(supportFragmentManager, "People picker")
        }

        place.setOnClickListener {
            // Set the fields to specify which types of place data to return.
            val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }

        val buttonCreate : Button = findViewById(R.id.create_event)
        buttonCreate.setOnClickListener {
            event.name = name.text.toString()
            event.description = description.text.toString()
            event.place = placeId!!
            val formEvent = FormCreateEvent(
                event.name,
                event.sport.getNameSport(),
                event.date,
                event.time,
                event.place,
                event.description,
                event.nbPeople,
                event.privacy
            )
            if(formEvent.isFormValid()){
                error.visibility = View.GONE
                val newEvent = Event(
                    Utils().generatePassword(75),
                    event.name,
                    event.sport,
                    event.date,
                    event.time,
                    event.place,
                    event.nbPeople,
                    event.description,
                    event.privacy,
                    SessionUser(this).getIdFromUser()
                )
                newEvent.insertEvent(this)
                startActivity(Intent(this, MainActivity::class.java))
            }
            else{
                error.visibility = View.VISIBLE
            }
        }
    }

    override fun onTimeSet(p0: android.widget.TimePicker?, p1: Int, p2: Int) {
        val textView = findViewById<TextView>(R.id.time) as TextView
        textView.text = "${p1}:$p2"
        event.time = textView.text.toString()
    }

    override fun onDateSet(p0: android.widget.DatePicker?, p1: Int, p2: Int, p3: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        c.set(Calendar.MONTH, p2)
        c.set(Calendar.DAY_OF_MONTH, p3)
        //Format --> convert date to string
        //getDateInstance (SORT) --> dd/MM/YYYY
        val currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.time)

        val textView = findViewById<View>(R.id.date) as TextView
        textView.text = currentDateString
        event.date = textView.text.toString()
    }

    private fun initializeFields(){
        if(event.name.isNotEmpty()){
            name.setText(event.name)
        }
        if(event.sport != Sport.INIT){
            autoCompleteSport.text = event.sport.getNameSport()
            autoCompleteSport.setCompoundDrawablesWithIntrinsicBounds(event.sport.getLogoSport(), 0, 0, 0)
        }
        if(event.place.isNotEmpty()){
            placeId = event.place
            val gg = SessionGooglePlace(this)
            gg.init()
            val placesClient = gg.createClient()
            val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
            val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(placeId!!, placeFields)

            placesClient.fetchPlace(request)
                .addOnSuccessListener {
                    val p : Place = it.place
                    place.text = p.name
                }
                .addOnFailureListener {
                }
        }
        if(event.date.isNotEmpty()){
            date.text = event.date
        }
        if(event.time.isNotEmpty()){
            time.text = event.time
        }
        if(event.nbPeople > 0){
            numberPeople.text = event.nbPeople.toString()
        }
        if(event.description.isNotEmpty()){
            description.setText(event.description)
        }
        if(event.privacy != Privacy.INIT){
            privacy.text = event.privacy.toString()
        }
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        if(p0 != null){
            if(p0.maxValue == 22) {
                numberPeople.text = p0.value.toString()
                event.nbPeople = p0.value
            }
            else{
                privacy.text = stringPrivacy[p0.value]
                event.privacy = Privacy.INIT.valueOfString(stringPrivacy[p0.value])
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                val p = Autocomplete.getPlaceFromIntent(data!!)
                placeId = p.id
                place.text = p.name
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                println("ERROR : ${status.statusMessage}")
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}
