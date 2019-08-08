package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.session.SessionUser
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener

class ActivityInfoUser : AppCompatActivity(), View.OnClickListener {
    private val session : SessionUser = SessionUser()
    private lateinit var info : LinearLayout
    private lateinit var deconnexion : LinearLayout
    private lateinit var friends : LinearLayout

    private lateinit var googleSignInClient : GoogleSignInClient

    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val checkAccountIntent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_user)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val nameAndFirstnameTextView = findViewById<TextView>(R.id.name_and_firstName_account)
        val photoImageView = findViewById<ImageView>(R.id.profile_photo)

        info = findViewById(R.id.infos)
        deconnexion = findViewById(R.id.deconnexion)
        friends = findViewById(R.id.friends_layout)
        info.setOnClickListener(this)
        deconnexion.setOnClickListener(this)
        friends.setOnClickListener(this)

        /* GOOGLE LOGOUT */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        nameAndFirstnameTextView.text = session.identity(
            session.getIdFromUser(),
            nameAndFirstnameTextView
        ).toString()

        session.showPhotoUser(this, photoImageView)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.infos -> {
                val checkAccountIntent = Intent(this, UserInfoActivity::class.java)
                startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.deconnexion -> {
                session.signOut()
                googleSignInClient.signOut()
                LoginManager.getInstance().logOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
                startActivity(intent)
            }
            R.id.friends_layout -> {
                startActivity(Intent(this, FriendsActivity::class.java))
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
        }
    }

}
