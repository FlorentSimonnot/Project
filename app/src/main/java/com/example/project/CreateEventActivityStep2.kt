package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.example.events.Event
import com.example.events.Privacy
import com.example.form.FormCreateEvent2
import com.example.picker.NumberPickerCustom
import com.example.picker.StringPickerCustom
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.utils.Utils
import org.w3c.dom.Text

class CreateEventActivityStep2 : AppCompatActivity(), NumberPicker.OnValueChangeListener {
    private lateinit var textViewNumberPeople : TextView
    private lateinit var textViewPrivacy : TextView
    private lateinit var stringPrivacy : Array<String>
    private val session = SessionUser(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event_step2)

        val action = intent.action
        val infos : Bundle? = intent.extras
        val place = infos?.get("place").toString()
        val name = infos?.get("name").toString()
        val date = infos?.get("date").toString()
        val time = infos?.get("time").toString()
        val sport = infos?.get("sport").toString()

        textViewNumberPeople = findViewById(R.id.numberPeople)
        textViewPrivacy = findViewById(R.id.privacy)

        stringPrivacy = arrayOf("Public", "Private", "Only invitation")

        textViewNumberPeople.setOnClickListener {
            val numberPicker = NumberPickerCustom(1, 22, "Choose max number people", "Select a value")
            numberPicker.setValueChangeListener(this)
            numberPicker.show(supportFragmentManager, "People picker")
        }

        textViewPrivacy.setOnClickListener {
            val numberPicker = StringPickerCustom(0, 2, "Choose max number people", "Select a value", stringPrivacy)
            numberPicker.setValueChangeListener(this)
            numberPicker.show(supportFragmentManager, "People picker")
        }

        findViewById<Button>(R.id.create_event).setOnClickListener {
            val form2 = FormCreateEvent2(
                textViewNumberPeople.text.toString().toInt(),
                findViewById<TextView>(R.id.description).text.toString(),
                textViewPrivacy.text.toString()
            )
            if(form2.isFormValid()){
                val event = Event(
                    Utils().generatePassword(70),
                    name,
                    Sport.valueOf(sport),
                    date,
                    time,
                    place,
                    textViewNumberPeople.text.toString().toInt(),
                    findViewById<TextView>(R.id.description).text.toString(),
                    Privacy.INIT.valueOfString(textViewPrivacy.text.toString()),
                    session.getIdFromUser()
                )
                event.insertEvent(this)
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        if(p0 != null){
            if(p0.maxValue == 22) {
                textViewNumberPeople.text = p0.value.toString()
            }
            else{
                textViewPrivacy.text = stringPrivacy[p0.value]
            }
        }
    }
}
