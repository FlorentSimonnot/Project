package com.example.project

import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.events.EventFirstStep
import com.example.events.PlaceEvent
import com.example.events.Privacy
import com.example.picker.DatePicker
import com.example.picker.NumberPickerCustom
import com.example.picker.StringPickerCustom
import com.example.picker.TimePicker
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.example.sport.Sport
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_create_event.*
import kotlinx.android.synthetic.main.activity_create_event.description
import kotlinx.android.synthetic.main.activity_create_event.numberPeople
import kotlinx.android.synthetic.main.activity_modify_event.*
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
    private val apiKey = "AIzaSyDdY6X8SWrQv4o8bR2dM_c8AX7C2-4n434"
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var placeId : String? = ""
    private lateinit var modifyTitle : TextView
    private lateinit var modifyDescription : TextView
    private lateinit var modifyPrivacy : TextView
    private lateinit var modifyNumberOfParticipants: TextView
    private lateinit var modifySport : TextView
    private lateinit var modifyDate : TextView
    private lateinit var modifyHour : TextView
    private lateinit var modifyPlace : TextView
    private var stringPrivacy : ArrayList<String> = ArrayList()
    private lateinit var stringPrivacyArray : Array<String>
    private var privacys : ArrayList<Privacy> = ArrayList()
    private var event : EventFirstStep = EventFirstStep()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_event)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.modify_event_title)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        keyEvent = infos?.getString("key").toString()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }


        /*Init xml elements*/
        val buttonPrivacy = findViewById<LinearLayout>(R.id.button_privacy)
        val buttonNumberOfParticipant = findViewById<LinearLayout>(R.id.button_number_of_participant)
        val buttonConfirm = findViewById<ImageButton>(R.id.confirm_edit)


        modifyTitle = findViewById(R.id.modify_title)
        modifyDescription = findViewById(R.id.modify_description)
        modifyNumberOfParticipants = findViewById(R.id.modify_nb_people)
        modifyPrivacy = findViewById(R.id.modify_privacy)
        modifySport = findViewById(R.id.modify_sport)
        modifyDate = findViewById(R.id.modify_date)
        modifyHour = findViewById(R.id.modify_hour)
        modifyPlace = findViewById(R.id.modify_place)

        /*Set onclick*/
        modifyTitle.setOnClickListener(this)
        modifySport.setOnClickListener(this)
        modifyDate.setOnClickListener(this)
        modifyHour.setOnClickListener(this)
        modifyDescription.setOnClickListener(this)
        buttonNumberOfParticipant.setOnClickListener(this)
        buttonPrivacy.setOnClickListener(this)
        buttonConfirm.setOnClickListener(this)

        Privacy.values().forEach {
            privacys.add(it)
        }

        stringPrivacyArray = Array(privacys.size) {""}
        privacys.forEachIndexed {index, it ->
            if(it != Privacy.INIT) {
                stringPrivacy.add(it.namePrivacy(this))
                stringPrivacyArray[index] = it.namePrivacy(this)
            }
        }

        createSportList()

        if(intent.hasExtra("event")){
            event = intent.getSerializableExtra("event") as EventFirstStep
            initializeFields()
        }
        else{
            initializeFields()
        }

        modifyPlace.setOnClickListener {
            // Set the fields to specify which types of place data to return.
            val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }

        modifySport.setOnClickListener{
            val intent = Intent(this, SportActivity::class.java)
            event.name = modifyTitle.text.toString()
            event.description = modifyDescription.text.toString()
            event.place.idPlace = modifyPlace.text.toString()
            event.nbPeople = modifyNumberOfParticipants.text.toString().toInt()
            event.privacy = Privacy.INIT.valueOfString(modifyPrivacy.text.toString())
            event.date = modifyDate.text.toString()
            event.time = modifyHour.text.toString()
            event.sport = Sport.INIT.getString(this, modifySport.text.toString())
            intent.putExtra("event", event)
            intent.putExtra("comeFrom", "ModifyEventActivity")
            intent.putExtra("placeId", placeId!!)
            intent.putExtra("key", keyEvent)
            finish()
            startActivity(intent)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.modify_title -> {
                val dialog = AlertCustomWithEditText(
                    this,
                    R.layout.layout_dialog_with_textview,
                    modifyTitle,
                    getString(R.string.modify_event_name_alert_title),
                    getString(R.string.modify_event_name_alert_message),
                    keyEvent,
                    "name"
                )
                dialog.show(supportFragmentManager, "Title")
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
                    getString(R.string.modify_event_description_alert_title),
                    getString(R.string.modify_event_description_alert_message),
                    keyEvent,
                    "description"
                )
                dialog.show(supportFragmentManager, "Description")
            }
            R.id.button_number_of_participant -> {
                val numberPicker = NumberPickerCustom(1, 22, getString(R.string.modify_event_participants_alert_title), getString(R.string.modify_event_participants_alert_message), modifyNumberOfParticipants.text.toString().toInt())
                numberPicker.setValueChangeListener(this)
                numberPicker.show(supportFragmentManager, "People picker")
            }
            R.id.button_privacy -> {
                val numberPicker = StringPickerCustom(0, 2, getString(R.string.modify_event_privacy_alert_title), getString(R.string.modify_event_privacy_alert_message), stringPrivacyArray, modifyPrivacy.text.toString())
                numberPicker.setValueChangeListener(this)
                numberPicker.show(supportFragmentManager, "People picker")
            }
            R.id.confirm_edit -> {
                Event(
                    keyEvent,
                    modifyTitle.text.toString(),
                    Sport.INIT.getString(this, modifySport.text.toString()),
                    event.getDate(),
                    PlaceEvent(placeId!!),
                    modifyNumberOfParticipants.text.toString().toInt(),
                    modifyDescription.text.toString(),
                    Privacy.INIT.valueOfString(modifyPrivacy.text.toString()),
                    SessionUser(this).getIdFromUser()
                ).updateEvent(this)
            }
        }
    }

    override fun onTimeSet(p0: android.widget.TimePicker?, p1: Int, p2: Int) {
        val textView : TextView = findViewById(R.id.modify_hour)
        textView.text = "${p1}:$p2"
        event.time = textView.text.toString()
    }

    override fun onDateSet(p0: android.widget.DatePicker?, p1: Int, p2: Int, p3: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        c.set(Calendar.MONTH, p2)
        c.set(Calendar.DAY_OF_MONTH, p3)
        val currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.time)

        val textView = findViewById<TextView>(R.id.modify_date)
        textView.text = currentDateString
        event.date = textView.text.toString()
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
                event.nbPeople = p0.value
            }
            else{
                modifyPrivacy.text = stringPrivacy[p0.value]
                event.privacy = privacys[p0.value]
            }
        }
    }

    override fun applyText(title: String, textView: TextView, action: String) {
        when(action){
            "name" -> {
                event.name = title
            }
            "description" -> {
                event.description = title
            }
        }
        textView.text = title
    }

    private fun initializeFields(){
        if(event.name.isNotEmpty()){
            modifyTitle.text = event.name
        }else{
            Event().writeInfoEvent(this, keyEvent, modifyTitle, "name")
        }

        if(event.sport != Sport.INIT){
            modifySport.text = event.sport.getNameSport(this)
            modifySport.setCompoundDrawablesWithIntrinsicBounds(0, 0, event.sport.getLogoSport(), 0)
            modifySport.compoundDrawablePadding = 10
        }else{
            Event().writeInfoEvent(this, keyEvent, modifySport, "sport")
        }

        if(event.place.idPlace.isNotEmpty()){
            modifyPlace.text = event.place.address
        }else{
            Event().writeInfoEvent(this, keyEvent, modifyPlace, "place")
        }

        if(event.date.isNotEmpty()){
            modifyDate.text = event.date
        }else{
            Event().writeInfoEvent(this, keyEvent, modifyDate, "date")
        }

        if(event.time.isNotEmpty()){
            modifyHour.text = event.time
        }else{
            Event().writeInfoEvent(this, keyEvent, modifyHour, "time")
        }
        if(event.nbPeople > 0){
            modifyNumberOfParticipants.text = event.nbPeople.toString()
        }else{
            Event().writeInfoEvent(this, keyEvent, modifyNumberOfParticipants, "numberOfParticipants")
        }

        if(event.description.isNotEmpty()){
            modifyDescription.setText(event.description)
        }else{
            Event().writeInfoEvent(this, keyEvent, modifyDescription, "description")
        }

        if(event.privacy != Privacy.INIT){
            modifyPrivacy.text = event.privacy.toString()
        }else{
            Event().writeInfoEvent(this, keyEvent, modifyPrivacy, "privacy")
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                val p = Autocomplete.getPlaceFromIntent(data!!)
                placeId = p.id
                modifyPlace.text = p.name
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                println("ERROR : ${status.statusMessage}")
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
