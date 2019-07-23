package com.example.project

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.autocompleteAdapterCustom.AutocompleteSportAdapter
import com.example.sport.ItemSport
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.provider.Contacts.People
import android.widget.AdapterView




class CreateEventActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private val session = SessionUser()
    private var sportList : ArrayList<ItemSport> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        var autoCompleteSport : AutoCompleteTextView = findViewById(R.id.sport)
        var sport : ItemSport = ItemSport("", -1)
        createSportList()

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

        val adapterSport = AutocompleteSportAdapter(
            this,
            sportList as List<ItemSport>
        )
        autoCompleteSport.setAdapter(adapterSport)
        autoCompleteSport.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, pos, id ->
            sport = adapterView.getItemAtPosition(pos) as ItemSport
        }

        /* When data is change, add or delete from autocompleteTextView */
        autoCompleteSport.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                autoCompleteSport.setCompoundDrawablesWithIntrinsicBounds(Sport.INIT.whichSport(p0!!.toString().toUpperCase()).getLogo(), 0, 0, 0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Nothing
            }

            /* Remove the sport logo if user erase his choice */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                /*if(p0!!.isEmpty()){
                    autoCompleteSport.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
                adapterSport.filter.filter(p0);*/
            }

        });

        val buttonCreate : Button = findViewById(R.id.create_event)
        buttonCreate.setOnClickListener {
            val form = FormCreateEvent(
                findViewById<EditText>(R.id.name_event).text.toString(),
                sport.getSportName(),
                findViewById<EditText>(R.id.date).text.toString(),
                findViewById<EditText>(R.id.place).text.toString(),
                findViewById<EditText>(R.id.number).text.toString().toInt(),
                findViewById<EditText>(R.id.description).text.toString(),
                findViewById<Spinner>(R.id.privacy).selectedItemId.toString()
            )

            if(form.isFormValid()){
                val event = Event(
                    Utils().generatePassword(70),
                    findViewById<EditText>(R.id.name_event).text.toString(),
                    Sport.INIT.whichSport(findViewById<EditText>(R.id.sport).text.toString().toUpperCase()),
                    findViewById<EditText>(R.id.date).text.toString(),
                    findViewById<EditText>(R.id.place).text.toString(),
                    findViewById<EditText>(R.id.number).text.toString().toInt(),
                    findViewById<EditText>(R.id.description).text.toString(),
                    Privacy.INIT.whichPrivacy(findViewById<Spinner>(R.id.privacy).selectedItemId.toInt()),
                    session.getIdFromUser()
                )
                event.insertEvent()
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
        Sport.values().forEach {
            if(it != Sport.INIT) {
                sportList.add(ItemSport(it.toString().toLowerCase(), it.getLogo()))
            }
        }
    }

}
