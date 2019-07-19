package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.events.Event
import com.example.events.Privacy
import com.example.events.Sport
import com.example.form.FormCreateEvent
import com.example.session.SessionUser
import com.example.utils.Utils

class CreateEventActivity : AppCompatActivity() {
    private val session = SessionUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        var autoCompleteSport : AutoCompleteTextView = findViewById(R.id.sport)
        var sport : String = ""


        val adapterSport = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            Sport.values()
        )
        autoCompleteSport.setAdapter(adapterSport)

        autoCompleteSport.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view ,position,id->
            val selectedItem = parent.getItemAtPosition(position).toString()
            sport = selectedItem
        }

        val buttonCreate : Button = findViewById(R.id.create_event)
        buttonCreate.setOnClickListener {
            val form = FormCreateEvent(
                findViewById<EditText>(R.id.name_event).text.toString(),
                sport,
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
}
