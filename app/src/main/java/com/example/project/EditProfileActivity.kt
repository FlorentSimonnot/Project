package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.session.SessionUser

class EditProfileActivity : AppCompatActivity() {
    var session : SessionUser = SessionUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val nameEditText = findViewById<EditText>(R.id.name_account)
        val firstNameEditText = findViewById<EditText>(R.id.firstName_account)
        val emailEditText = findViewById<EditText>(R.id.email_account)
        /*val sexSpinner = findViewById<Spinner>(R.id.sex_spinner)*/
        val birthdayEditText = findViewById<EditText>(R.id.birthday_account)
        val cityEditText = findViewById<EditText>(R.id.city_account)
        val describeEditText = findViewById<EditText>(R.id.describe_account)
        val modifyPasswordButton = findViewById<Button>(R.id.modify_password_button)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nameEditText.hint = session.writeInfoUser(
            session.getIdFromUser(),
            nameEditText,
            "name"
        ).toString()

        firstNameEditText.hint = session.writeInfoUser(
            session.getIdFromUser(),
            firstNameEditText,
            "firstName"
        ).toString()


        emailEditText.hint = session.writeInfoUser(
            session.getIdFromUser(),
            emailEditText,
            "email"
        ).toString()

        birthdayEditText.hint = session.writeInfoUser(
            session.getIdFromUser(),
            birthdayEditText,
            "birthday"
        ).toString()


        cityEditText.hint = session.writeInfoUser(
            session.getIdFromUser(),
            cityEditText,
            "city"
        ).toString()


        describeEditText.hint = session.writeInfoUser(
            session.getIdFromUser(),
            describeEditText,
            "describe"
        ).toString()

        modifyPasswordButton.setOnClickListener {
            startActivity(Intent(this, ModifyPasswordActivity::class.java))
        }

    }

}
