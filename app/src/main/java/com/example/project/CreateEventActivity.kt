package com.example.project

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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



class CreateEventActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private val session = SessionUser()
    private var sportList : ArrayList<Sport> = ArrayList()
    private lateinit var autoCompleteSport : TextView
    private lateinit var sport : Sport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        autoCompleteSport = findViewById(R.id.sport)

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

        val cancelBtn = findViewById<Button>(R.id.btnCancelDialog)
        cancelBtn.setOnClickListener {
            findViewById<RelativeLayout>(R.id.dialog).alpha = 0F
        }

        autoCompleteSport.setOnClickListener{
            findViewById<RelativeLayout>(R.id.dialog).alpha = 100F
            val adapter = ArrayAdapterSport(this, R.layout.list_item_sport, sportList)
            val listView = findViewById<ListView>(R.id.listView)
            listView.adapter = adapter
            listView.setOnItemClickListener { adapterView, view, i, l ->
                sport = Sport.valueOf(sportList[i].toString())
                autoCompleteSport.text = sport.name
                autoCompleteSport.setCompoundDrawablesWithIntrinsicBounds(sport.getLogo(), 0, 0, 0)
                listView.adapter = null
                findViewById<RelativeLayout>(R.id.dialog).alpha = 0F
            }
        }

        val buttonCreate : Button = findViewById(R.id.create_event)
        buttonCreate.setOnClickListener {
            val form = FormCreateEvent(
                findViewById<EditText>(R.id.name_event).text.toString(),
                "Football",
                findViewById<TextView>(R.id.date).text.toString(),
                findViewById<TextView>(R.id.place).text.toString()
            )

            if(form.isFormValid()){
                val intent = Intent(this, CreateEventActivityStep2::class.java)
                intent.putExtra("name", form.name)
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
