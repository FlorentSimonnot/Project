package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.user.User
import kotlinx.android.synthetic.main.activity_next_sign_in_jojo.*
import java.lang.StringBuilder

class PublicUserActivity : AppCompatActivity() {
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_user)

        user = intent.getSerializableExtra("user") as User

        val identityTextView = findViewById<TextView>(R.id.identity_public_user)
        var identityBuilder = StringBuilder()
        val eventCreatedTextView = findViewById<TextView>(R.id.events_created_textview)
        val eventJoinedTextView = findViewById<TextView>(R.id.events_joined_textview)
        val sexTextView = findViewById<TextView>(R.id.user_sex_textview)
        val birthdayTextView = findViewById<TextView>(R.id.user_birthday_textview)
        val cityTextView = findViewById<TextView>(R.id.city_textview)
        val descriptionTextView = findViewById<TextView>(R.id.description_textview)

        identityBuilder.append(user.firstName).append(" ").append(user.name)
        identityTextView.text = identityBuilder

        eventCreatedTextView.text = user.eventsCreated.size.toString()
        eventJoinedTextView.text = user.eventsJoined.size.toString()
        sexTextView.text = user.sex.toString()
        birthdayTextView.text = user.birthday
        cityTextView.text = user.city
        descriptionTextView.text = user.description
    }
}

