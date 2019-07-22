package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.session.SessionUser
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener

class UserInfoActivity : AppCompatActivity(), View.OnClickListener {
    var session : SessionUser = SessionUser()
    private lateinit var googleSignInClient : GoogleSignInClient

    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val checkAccountIntent = Intent(this, MainActivity::class.java)
                startActivity(checkAccountIntent)
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
        setContentView(R.layout.activity_user_info)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        /* GOOGLE LOGOUT */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        /* BUTTON ACTIONS */
        findViewById<ImageButton>(R.id.btn_logout).setOnClickListener(this)
        findViewById<Button>(R.id.btn_delete_account).setOnClickListener(this)
        findViewById<Button>(R.id.edit_profile_button).setOnClickListener(this)

        val nameTextView = findViewById<TextView>(R.id.name_account)
        val firstNameTextView = findViewById<TextView>(R.id.firstName_account)
        val emailTextView = findViewById<TextView>(R.id.email_account)
        val sexTextView = findViewById<TextView>(R.id.sex_account)
        val birthdayTextView = findViewById<TextView>(R.id.birthday_account)
        val cityTextView = findViewById<TextView>(R.id.city_account)
        val describeTextView = findViewById<TextView>(R.id.describe_account)

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

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_logout -> {
                session.signOut()
                googleSignInClient.signOut()
                LoginManager.getInstance().logOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.edit_profile_button -> {
                startActivity(Intent(this, EditProfileActivity::class.java))
            }
            R.id.btn_delete_account -> {
                session.deleteUser(this)
            }
        }
    }
}
