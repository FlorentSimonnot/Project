package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.session.SessionUser


class MainActivity : AppCompatActivity() {
    private lateinit var textMessage: TextView
    var session : SessionUser = SessionUser()
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.text = R.string.bottom_navigation_menu_home.toString()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                textMessage.text = R.string.bottom_navigation_menu_map.toString()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                textMessage.text = R.string.bottom_navigation_menu_account.toString()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                textMessage.text = R.string.bottom_navigation_menu_chat.toString()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)

        if (session.isLogin()) {
            println("   YOU ARE ALREADY CONNECTED !!")
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            //Flags allow to block come back
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        println("What's up ${session.getCurrentUser()}")

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val btnSearch : ImageButton = findViewById(R.id.btn_search)
        val btnSettings : ImageButton = findViewById(R.id.btn_settings)
        val btnSignOut : Button = findViewById(R.id.btnSignOut)

        btnSearch.setOnClickListener {
            val modal : LinearLayout = findViewById(R.id.search_layout)
            if(modal.alpha == 1F){
                modal.alpha = 0F
            }
            else{
                modal.alpha = 1F
            }
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btnSignOut.setOnClickListener {
            session.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


        /*test de mes layout*/
        val sign_in_jojo = findViewById<Button>(R.id.sign_in_jojo)
        sign_in_jojo.setOnClickListener {
            val next_sign_in_jojo = Intent(this, LoginActivity::class.java)
            startActivity(next_sign_in_jojo)
        }


    }

    override fun onStart() {
        super.onStart()
    }


}
