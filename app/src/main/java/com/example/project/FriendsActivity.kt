package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.session.SessionUser

class FriendsActivity : AppCompatActivity() {
    var session = SessionUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        val friendsTextView = findViewById<TextView>(R.id.friends_textview)
        val waitingTextView = findViewById<TextView>(R.id.waiting_textview)

        session.countFriends(friendsTextView, waitingTextView)

        friendsTextView.setOnClickListener {
            startActivity(Intent(this, FriendsListActivity::class.java))
        }

        waitingTextView.setOnClickListener {
            startActivity(Intent(this, WaitingListActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
