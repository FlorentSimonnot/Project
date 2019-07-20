package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.session.SessionUser
import com.example.user.User
import kotlinx.android.synthetic.main.activity_next_sign_in_jojo.*
import kotlinx.android.synthetic.main.activity_user_info.*

class UserInfoActivity : AppCompatActivity() {
    var session : SessionUser = SessionUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val toolbarTextView = findViewById<TextView>(R.id.toolbar_textview)
        val nameTextView = findViewById<TextView>(R.id.name_account)
        val firstNameTextView = findViewById<TextView>(R.id.firstName_account)
        val emailTextView = findViewById<TextView>(R.id.email_account)
        val sexTextView = findViewById<TextView>(R.id.sex_account)
        val birthdayTextView = findViewById<TextView>(R.id.birthday_account)
        val cityTextView = findViewById<TextView>(R.id.city_account)
        val describeTextView = findViewById<TextView>(R.id.describe_account)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val editProfileButton = findViewById<Button>(R.id.edit_profile_button)
        val logOutButton = findViewById<Button>(R.id.log_out_button)

        toolbarTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            toolbarTextView,
            "firstName",
            ""
        ).toString()

        nameTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            nameTextView,
            "name",
            ""
        ).toString()

        firstNameTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            firstNameTextView,
            "firstName",
            ""
        ).toString()


        emailTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            emailTextView,
            "email",
            ""
        ).toString()


        sexTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            sexTextView,
            "sex",
            ""
        ).toString()


        birthdayTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            birthdayTextView,
            "birthday",
            ""
        ).toString()


        cityTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            cityTextView,
            "city",
            ""
        ).toString()


        describeTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            describeTextView,
            "describe",
            ""
        ).toString()

        editProfileButton.setOnClickListener {
            val editProfileIntent = Intent(this, EditProfileActivity::class.java)
            startActivity(editProfileIntent)
        }

        logOutButton.setOnClickListener {
            val logOutIntent = Intent(this, LoginActivity::class.java)
            session.signOut()
            startActivity(logOutIntent)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
