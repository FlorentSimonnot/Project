package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.session.SessionUser
import com.example.user.User

class UserInfoActivity : AppCompatActivity() {
    var session : SessionUser = SessionUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val userTextView = findViewById<TextView>(R.id.account_name)
        userTextView.text = session.writeInfoUser(session.getIdFromUser(), userTextView, "firstName", "").toString()
    }
}
