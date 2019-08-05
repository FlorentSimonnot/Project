package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.dialog.AlertDialogCustom
import com.example.events.Event
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventInfoJojoActivity : AppCompatActivity() {
    private val session = SessionUser()
    private lateinit var usersWaitingDialog : AlertDialogCustom
    private lateinit var usersDialog : AlertDialogCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info_jojo)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        val keyEvent = infos?.getString("key").toString()

        val titleTextView : TextView = findViewById(R.id.event_name)
        val sportLogoImageView = findViewById<ImageView>(R.id.sport_logo)
        val sportTextView = findViewById<TextView>(R.id.sport)
        val creatorTextView = findViewById<TextView>(R.id.creator)
        val placeTextView = findViewById<TextView>(R.id.place)
        val dateTextView = findViewById<TextView>(R.id.date)
        val timeTextView = findViewById<TextView>(R.id.time)
        val nbPeopleTextView = findViewById<TextView>(R.id.nb_people)
        val descriptionTextView = findViewById<TextView>(R.id.description)
        val button_delete = findViewById<ImageButton>(R.id.button_delete)
        val button_edit = findViewById<ImageButton>(R.id.button_edit)
        val button_participate = findViewById<Button>(R.id.button_participate)
        val button_cancel = findViewById<Button>(R.id.button_cancel)
        val participantsWaiting = findViewById<TextView>(R.id.participants_waiting)
        val participantsConfirmed = findViewById<TextView>(R.id.participants)

        Event().verifyCurrentUserIsCreator(keyEvent, participantsWaiting, session)


        Event().writeInfoEvent(
            this,
            keyEvent,
            titleTextView,
            "name"
        ).toString()

        Event().writeInfoEvent(
            this,
            keyEvent,
            sportTextView,
            "sport"
        )

        Event().writeLogoSport(this, keyEvent, sportLogoImageView)

        Event().writeInfoEvent(
            this,
            keyEvent,
            creatorTextView,
            "creator"
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


        Event().getButton(this, keyEvent, button_edit, button_delete, button_participate, button_cancel)

        button_edit.setOnClickListener {

        }

        button_delete.setOnClickListener {
            val deleteEventAlertDialog = AlertDialog.Builder(this)
            deleteEventAlertDialog.setTitle("Are you sure?")
            deleteEventAlertDialog.setMessage("Your event will be deleted.\nConfirm?")
            deleteEventAlertDialog.setPositiveButton("Yes"){_, _ ->
                Event().deleteEvent(this, keyEvent, session)
                startActivity(Intent(this, MainActivity::class.java))
            }
            deleteEventAlertDialog.setNegativeButton("No"){_, _ ->

            }
            deleteEventAlertDialog.show()
        }

        button_participate.setOnClickListener {
            Event().participateEvent(this, keyEvent, session)
        }

        button_cancel.setOnClickListener {
            Event().cancelParticipation(this, keyEvent, session)
        }

        participantsWaiting.setOnClickListener {
            val intent = Intent(this, ParticipantsWaitedActivity::class.java)
            intent.putExtra("key", keyEvent)
            finish()
            startActivity(intent)
            overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
        }

        participantsConfirmed.setOnClickListener {
            val intent = Intent(this, ParticipantsActivity::class.java)
            intent.putExtra("key", keyEvent)
            finish()
            startActivity(intent)
            overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        EventInfoJojoActivity::finish
        startActivity(intent)
        return true
    }

}
