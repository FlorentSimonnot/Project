package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.example.events.Event
import com.example.events.Privacy
import com.example.sport.Sport
import com.example.form.FormCreateEvent
import com.example.picker.TimePicker
import com.example.picker.DatePicker
import com.example.session.SessionUser
import com.example.utils.Utils
import android.widget.TextView
import com.example.arrayAdapterCustom.ArrayAdapterSport
import com.example.sport.ItemSport
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import android.R.attr.apiKey
import android.app.*
import com.example.dialog.AlertDialogCustom
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Arrays.asList
import com.google.android.libraries.places.widget.AutocompleteSupportFragment







class CreateEventActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private var sportList : ArrayList<Sport> = ArrayList()
    private lateinit var autoCompleteSport : TextView
    private lateinit var listView : ListView
    private val API_KEY = "AIzaSyDdY6X8SWrQv4o8bR2dM_c8AX7C2-4n434"
    private var placeId : String? = ""
    private lateinit var dialogSport : AlertDialogCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        // Initialize the SDK
        Places.initialize(applicationContext, API_KEY)
        // Create a new Places client instance
        val placesClient = Places.createClient(this)
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment?.setHint("Search your place")


        // Specify the types of place data to return.
        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                println("AN ERROR OCCURED $p0")
                placeId = ""
            }

            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                placeId = place.id
            }

        })

        autoCompleteSport = findViewById(R.id.sport)
        createSportList()
        //Init sdialog sport
        dialogSport = AlertDialogCustom(this, R.layout.list_item_sport, sportList, "Choose sport", autoCompleteSport)

        val time = findViewById<TextView>(R.id.time)
        time.setOnClickListener {
            val timePicker = TimePicker()
            timePicker.show(supportFragmentManager, "Choose time")
        }

        val date = findViewById<TextView>(R.id.date)
        date.setOnClickListener {
            val datePicker = DatePicker()
            datePicker.show(supportFragmentManager, "Choose date")
        }

        val cancelBtn = findViewById<Button>(R.id.btnCancelDialog)
        cancelBtn.setOnClickListener {
            findViewById<RelativeLayout>(R.id.dialog).alpha = 0F
            findViewById<RelativeLayout>(R.id.bg).setBackgroundColor(R.color.colorGrey100)
            findViewById<RelativeLayout>(R.id.infoLayout).alpha = 100F
            listView = findViewById<ListView>(R.id.listView)
            listView.adapter = null
        }

        autoCompleteSport.setOnClickListener{
            dialogSport.createAlertDialog()
            dialogSport.showDialog()
        }

        val buttonCreate : Button = findViewById(R.id.create_event)
        buttonCreate.setOnClickListener {
            val form = FormCreateEvent(
                findViewById<EditText>(R.id.name_event).text.toString(),
                findViewById<TextView>(R.id.sport).text.toString(),
                findViewById<TextView>(R.id.date).text.toString(),
                findViewById<TextView>(R.id.time).text.toString(),
                placeId
            )

            if(form.isFormValid()){
                val intent = Intent(this, CreateEventActivityStep2::class.java)
                intent.putExtra("name", form.name)
                intent.putExtra("place", form.place)
                intent.putExtra("sport", form.sport)
                intent.putExtra("date", form.date)
                intent.putExtra("time", form.time)
                startActivity(intent)
            }
        }
    }

    override fun onTimeSet(p0: android.widget.TimePicker?, p1: Int, p2: Int) {
        val textView = findViewById<TextView>(R.id.time) as TextView
        textView.text = "${p1}:$p2"
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
    }


    private fun createSportList(){
        Sport.values().forEach{
            if(it != Sport.INIT){
                sportList.add(it)
            }
        }
    }
}
