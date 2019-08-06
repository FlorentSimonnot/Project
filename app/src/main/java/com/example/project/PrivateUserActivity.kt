package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.user.User
import java.lang.StringBuilder

class PrivateUserActivity : AppCompatActivity() {
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_user)

        user = intent.getSerializableExtra("user") as User

        val identityTextView = findViewById<TextView>(R.id.identity_private_user)
        var identityBuilder = StringBuilder()
        val eventCreatedTextView = findViewById<TextView>(R.id.events_created_textview)
        val eventJoinedTextView = findViewById<TextView>(R.id.events_joined_textview)
        val addButton = findViewById<Button>(R.id.add_button)


        identityBuilder.append(user.firstName).append(" ").append(user.name)
        identityTextView.text = identityBuilder

        eventCreatedTextView.text = user.eventsCreated.size.toString()
        eventJoinedTextView.text = user.eventsJoined.size.toString()

    }
}
