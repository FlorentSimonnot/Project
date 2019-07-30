package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.events.Event

class EventInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        val infos : Bundle? = intent.extras
        val keyEvent = infos?.getString("key").toString()
        val titleTextView : TextView = findViewById(R.id.titleEvent)

        Event().writeInfoEvent(this, keyEvent, titleTextView, "title")
    }
}
