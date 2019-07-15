package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignInJojoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_jojo)


        val next_button = findViewById<Button>(R.id.next)

        next_button.setOnClickListener {
            val next_sign_in_jojo = Intent(this, NextSignInJojoActivity::class.java)
            startActivity(next_sign_in_jojo)
        }
    }
}
