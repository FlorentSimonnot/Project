package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.events.Event

class EventInfoJojoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_jojo)

        val infos : Bundle? = intent.extras
        val keyEvent = infos?.getString("key").toString()

        val titleTextView : TextView = findViewById(R.id.event_name)
        val sportLogoImageView = findViewById<ImageView>(R.id.sport_logo)
        val sportTextView = findViewById<TextView>(R.id.sport)
        val creatorId = TextView(this)
        val creatorTextView = findViewById<TextView>(R.id.creator)
        val placeTextView = findViewById<TextView>(R.id.place)
        val dateTextView = findViewById<TextView>(R.id.date)
        val timeTextView = findViewById<TextView>(R.id.time)
        val nbPeopleTextView = findViewById<TextView>(R.id.nb_people)
        val descriptionTextView = findViewById<TextView>(R.id.description)


        Event().writeInfoEvent(
            this,
            keyEvent,
            titleTextView,
            "name"
        ).toString()

        sportLogoImageView.setImageDrawable(getDrawable(Event().getSport(sportTextView.text.toString()).getLogo()))

        Event().writeInfoEvent(
            this,
            keyEvent,
            sportTextView,
            "sport"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            creatorId,
            "creator"
        )

        Event().getCreator(
            creatorId.text.toString(),
            creatorTextView
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            placeTextView,
            "place"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            dateTextView,
            "date"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            timeTextView,
            "time"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            nbPeopleTextView,
            "nb_people"
        )

        Event().writeInfoEvent(
            this,
            keyEvent,
            descriptionTextView,
            "description"
        )

    }
}
