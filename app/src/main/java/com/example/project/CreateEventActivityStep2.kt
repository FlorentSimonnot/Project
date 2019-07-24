package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.example.events.Privacy
import com.example.picker.NumberPickerCustom
import com.example.picker.StringPickerCustom
import org.w3c.dom.Text

class CreateEventActivityStep2 : AppCompatActivity(), NumberPicker.OnValueChangeListener {
    private lateinit var textViewNumberPeople : TextView
    private lateinit var textViewPrivacy : TextView
    private lateinit var stringPrivacy : Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event_step2)

        textViewNumberPeople = findViewById<TextView>(R.id.numberPeople)
        textViewPrivacy = findViewById<TextView>(R.id.privacy)

        stringPrivacy = arrayOf("Public", "Private", "Only invitation")

        textViewNumberPeople.setOnClickListener {
            val numberPicker = NumberPickerCustom(1, 22, "Choose max number people", "Select a value")
            numberPicker.setValueChangeListener(this);
            numberPicker.show(supportFragmentManager, "People picker");

        }

        textViewPrivacy.setOnClickListener {
            val numberPicker = StringPickerCustom(0, 2, "Choose max number people", "Select a value", stringPrivacy)
            numberPicker.setValueChangeListener(this);
            numberPicker.show(supportFragmentManager, "People picker");

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
