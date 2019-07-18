package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.events.Event
import com.example.events.Privacy
import com.example.events.Sport
import com.example.form.FormCreateEvent

class CreateEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val buttonCreate : Button = findViewById(R.id.create_event)
        buttonCreate.setOnClickListener {
            val form = FormCreateEvent(
                findViewById<EditText>(R.id.name_event).text.toString(),
                findViewById<EditText>(R.id.sport).text.toString(),
                findViewById<EditText>(R.id.date).text.toString(),
                findViewById<EditText>(R.id.place).text.toString(),
                findViewById<EditText>(R.id.number).text.toString().toInt(),
                findViewById<EditText>(R.id.description).text.toString(),
                findViewById<Spinner>(R.id.privacy).selectedItemId.toString()
            )

            if(form.isFormValid()){
                println("SALUT !!!!!!!!!!!!!!!!")
                val event = Event(
                    findViewById<EditText>(R.id.name_event).text.toString(),
                    Sport.INIT.whichSport(findViewById<EditText>(R.id.sport).text.toString().toUpperCase()),
                    findViewById<EditText>(R.id.date).text.toString(),
                    findViewById<EditText>(R.id.place).text.toString(),
                    findViewById<EditText>(R.id.number).text.toString().toInt(),
                    findViewById<EditText>(R.id.description).text.toString(),
                    Privacy.INIT.whichPrivacy(findViewById<Spinner>(R.id.privacy).selectedItemId.toInt())
                )
                event.insertEvent()
            }
        }
    }
}
