package com.example.project

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.dialog.AlertCustomWithEditText
import com.example.dialog.AlertDialogCustom
import com.example.events.Event
import com.example.events.Privacy
import com.example.picker.DatePicker
import com.example.picker.NumberPickerCustom
import com.example.picker.StringPickerCustom
import com.example.picker.TimePicker
import com.example.session.SessionUser
import com.example.sport.Sport
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.w3c.dom.Text
import java.text.DateFormat
import java.util.*

class ModifyEventActivity : AppCompatActivity(),
    View.OnClickListener,
    TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener,
    NumberPicker.OnValueChangeListener,
    AlertCustomWithEditText.ExampleDialogListener
{
    private lateinit var keyEvent : String
    private var sportList : ArrayList<Sport> = ArrayList()
    private lateinit var listView : ListView
    private val API_KEY = "AIzaSyDdY6X8SWrQv4o8bR2dM_c8AX7C2-4n434"
    private var placeId : String? = ""
    private lateinit var dialogSport : AlertDialogCustom
    private lateinit var stringPrivacy : Array<String>
    private lateinit var modifyTitle : TextView
    private lateinit var modifyDescription : TextView
    private lateinit var modifyPrivacy : TextView
    private lateinit var modifyNumberOfParticipants: TextView
    private lateinit var modifySport : TextView
    private lateinit var modifyDate : TextView
    private lateinit var modifyHour : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_event)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Modify your event"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        keyEvent = infos?.getString("key").toString()


        // Initialize the SDK
        Places.initialize(applicationContext, API_KEY)
        // Create a new Places client instance
        val placesClient = Places.createClient(this)
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        //autocompleteFragment?.setHint("Search your place")

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


        /*Init xml elements*/
        val buttonPrivacy = findViewById<LinearLayout>(R.id.button_privacy)
        val buttonNumberOfParticipant = findViewById<LinearLayout>(R.id.button_number_of_participant)
        val buttonConfirm = findViewById<Button>(R.id.confirm_edit)


        modifyTitle = findViewById(R.id.modify_title)
        modifyDescription = findViewById(R.id.modify_description)
        modifyNumberOfParticipants = findViewById(R.id.modify_nb_people)
        modifyPrivacy = findViewById(R.id.modify_privacy)
        modifySport = findViewById(R.id.modify_sport)
        modifyDate = findViewById(R.id.modify_date)
        modifyHour = findViewById(R.id.modify_hour)

        /*Set onclick*/
        modifyTitle.setOnClickListener(this)
        modifySport.setOnClickListener(this)
        modifyDate.setOnClickListener(this)
        modifyHour.setOnClickListener(this)
        modifyDescription.setOnClickListener(this)
        buttonNumberOfParticipant.setOnClickListener(this)
        buttonPrivacy.setOnClickListener(this)
        buttonConfirm.setOnClickListener(this)


        createSportList()
        dialogSport = AlertDialogCustom(this, R.layout.list_item_sport, sportList, "Choose sport", modifySport)
        stringPrivacy = arrayOf("Public", "Private", "Only invitation")

        val event = Event()
        event.writeInfoEvent(
            this,
            keyEvent,
            modifyTitle,
            "name"
        )
        event.writeInfoEvent(
            this, keyEvent, modifySport, "sport"
        )
        event.writeInfoEvent(
            this,
            keyEvent,
            modifyDate,
            "date"
        )
        event.writeInfoEvent(
            this,
            keyEvent,
            modifyHour,
            "time"
        )
        event.writeInfoEvent(
            this, keyEvent, modifyDescription, "description"
        )
        event.writeInfoEvent(
            this, keyEvent, modifyPrivacy, "privacy"
        )
        event.writeInfoEvent(
            this, keyEvent, modifyNumberOfParticipants, "numberOfParticipants"
        )
        event.writePlace(this, keyEvent, autocompleteFragment!!)

    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, EventInfoJojoActivity::class.java)
        intent.putExtra("key", keyEvent)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ModifyEventActivity::finish
        startActivity(intent)
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.modify_title -> {
                val dialog = AlertCustomWithEditText(
                    this,
                    R.layout.layout_dialog_with_textview,
                    modifyTitle,
                    "Title",
                    "Change title for your event",
                    keyEvent,
                    "name"
                )
                dialog.show(supportFragmentManager, "Title")
            }
            R.id.modify_sport -> {
                dialogSport.createAlertDialog()
                dialogSport.showDialog()
            }
            R.id.modify_date -> {
                val datePicker = DatePicker()
                datePicker.show(supportFragmentManager, "Choose date")
            }
            R.id.modify_hour -> {
                val timePicker = TimePicker()
                timePicker.show(supportFragmentManager, "Choose time")
            }
            R.id.modify_description -> {
                val dialog = AlertCustomWithEditText(
                    this,
                    R.layout.layout_dialog_with_textview,
                    modifyDescription,
                    "Description",
                    "Change description for your event",
                    keyEvent,
                    "name"
                )
                dialog.show(supportFragmentManager, "Description")
            }
            R.id.button_number_of_participant -> {
                val numberPicker = NumberPickerCustom(1, 22, "Choose max number people", "Select a value", modifyNumberOfParticipants.text.toString().toInt())
                numberPicker.setValueChangeListener(this)
                numberPicker.show(supportFragmentManager, "People picker")
            }
            R.id.button_privacy -> {
                val numberPicker = StringPickerCustom(0, 2, "Choose max number people", "Select a value", stringPrivacy, modifyPrivacy.text.toString())
                numberPicker.setValueChangeListener(this)
                numberPicker.show(supportFragmentManager, "People picker")
            }
            R.id.confirm_edit -> {
                val event = Event(
                    keyEvent,
                    modifyTitle.text.toString(),
                    Sport.valueOf(modifySport.text.toString()),
                    modifyDate.text.toString(),
                    modifyHour.text.toString(),
                    placeId!!,
                    modifyNumberOfParticipants.text.toString().toInt(),
                    modifyDescription.text.toString(),
                    Privacy.INIT.valueOfString(modifyPrivacy.text.toString()),
                    SessionUser().getIdFromUser()
                )
                event.updateEvent(this)
            }
        }
    }

    override fun onTimeSet(p0: android.widget.TimePicker?, p1: Int, p2: Int) {
        val textView = findViewById<TextView>(R.id.modify_hour)
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

        val textView = findViewById<TextView>(R.id.modify_date)
        textView.text = currentDateString
    }

    private fun createSportList(){
        Sport.values().forEach{
            if(it != Sport.INIT){
                sportList.add(it)
            }
        }
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        if(p0 != null){
            if(p0.maxValue == 22) {
                modifyNumberOfParticipants.text = p0.value.toString()
            }
            else{
                modifyPrivacy.text = stringPrivacy[p0.value]
            }
        }
    }

    override fun applyText(title: String, textView: TextView) {
        textView.text = title
    }
}
