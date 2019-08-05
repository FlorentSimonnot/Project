package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        identityBuilder.append(user.firstName).append(" ").append(user.name)
        identityTextView.text = identityBuilder
    }
}
