package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.forEach
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_next_sign_in_jojo.*
import java.lang.StringBuilder

class PublicUserActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var user: UserWithKey
    var session = SessionUser(this)
    lateinit var tab : TabLayout
    lateinit var userKey : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_user)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        userKey = infos?.getString("user").toString()

        tab = findViewById(R.id.tab)

        //user = intent.getSerializableExtra("user") as UserWithKey

        val identityTextView = findViewById<TextView>(R.id.identity)
        val descriptionTextView = findViewById<TextView>(R.id.description)
        val addFriendButton = findViewById<Button>(R.id.add_friend_button)

        SessionUser(this).writeInfoUser(this, userKey, identityTextView, "identity")
        SessionUser(this).writeInfoUser(this, userKey, descriptionTextView, "describe")

        addFriendButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.add_friend_button -> {
                var userWithKey = UserWithKey(User(), userKey)
                userWithKey.addFriend(session)
            }
        }
    }
}

