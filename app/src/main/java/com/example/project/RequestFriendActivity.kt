package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.session.SessionUser
import com.example.user.User
import kotlinx.android.synthetic.main.activity_info_user.*

class RequestFriendActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var keyUser : String
    private lateinit var layout : FrameLayout
    private lateinit var imageBackground : ImageView
    private lateinit var imageProfile : ImageView
    private lateinit var name : TextView
    private lateinit var seeProfileButton : ImageButton
    private lateinit var acceptButton : Button
    private lateinit var refuseButton : Button
    private lateinit var session : SessionUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_friend)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.alpha = 0.96f
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        keyUser= infos?.getString("key").toString()

        layout = findViewById(R.id.layout)
        layout.alpha = 0.96f

        imageBackground = findViewById(R.id.imageBackground)
        imageProfile = findViewById(R.id.profile_photo)
        name = findViewById(R.id.name)
        seeProfileButton = findViewById(R.id.seeProfileButton)
        acceptButton = findViewById(R.id.button_accept_invitation)
        refuseButton = findViewById(R.id.button_refuse_invitation)

        seeProfileButton.setOnClickListener(this)
        acceptButton.setOnClickListener(this)
        refuseButton.setOnClickListener(this)

        User().showPhotoUser(this, imageBackground, keyUser)
        User().showPhotoUser(this, imageProfile, keyUser)
        User().writeIdentity(name, keyUser)

        session = SessionUser(this)

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.seeProfileButton -> {
                val intent = Intent(this, PublicUserActivity::class.java)
                intent.putExtra("user",  keyUser)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            R.id.button_accept_invitation -> {
                session.acceptFriend(this, keyUser)
                onBackPressed()
            }
            R.id.button_refuse_invitation -> {
                session.refuseFriend(this, keyUser)
                onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
