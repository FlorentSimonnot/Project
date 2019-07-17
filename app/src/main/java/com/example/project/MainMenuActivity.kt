package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.example.events.Event
import com.example.events.Sport

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val events = listOf<Event>(
            Event(
                StringBuilder("five a lognes"),
                Sport.FOOTBALL,
                StringBuilder("22/01/2020"),
                StringBuilder("Lognes"),
                10,
                StringBuilder("que des pros svp")
            ),
            Event(
                StringBuilder("ptit basket au calme"),
                Sport.BASKETBALL,
                StringBuilder("today"),
                StringBuilder("Busys"),
                10,
                StringBuilder("tous niveaux autorisés"))
        )

        val listView = findViewById<ListView>(R.id.events_listview)
        val adapter = ArrayAdapter<Event>(
            this,
            android.R.layout.simple_list_item_1,
            events
        )
        listView.adapter = adapter

        val createEvent = findViewById<Button>(R.id.create_event)
        createEvent.setOnClickListener {
            val createEventIntent = Intent(this, CreateEventActivity::class.java)
            startActivity(createEventIntent)
        }
    }
}
