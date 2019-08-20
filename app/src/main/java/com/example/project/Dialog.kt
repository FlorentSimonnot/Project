package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import com.example.session.SessionUser
import com.example.user.User
import com.google.android.material.bottomnavigation.BottomNavigationView

class Dialog : AppCompatActivity() {
    private lateinit var keyUser : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val infos : Bundle? = intent.extras
        keyUser = infos?.getString("keyUser").toString()

        SessionUser().writeNameUserDiscussion(this, keyUser, supportActionBar!!)
    }
}
