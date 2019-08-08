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

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val friendsTextView = findViewById<TextView>(R.id.friends_textview)
        val waitingTextView = findViewById<TextView>(R.id.waiting_textview)

        session.countFriends(friendsTextView)
        session.countWaiting(waitingTextView)

        friendsTextView.setOnClickListener {
            startActivity(Intent(this, FriendsListActivity::class.java))
        }

        waitingTextView.setOnClickListener {
            startActivity(Intent(this, WaitingListActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val intent = Intent(this, ActivityInfoUser::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        EventInfoJojoActivity::finish
        startActivity(intent)
        return true
    }

}
