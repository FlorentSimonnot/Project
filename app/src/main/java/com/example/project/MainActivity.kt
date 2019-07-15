package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
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
        setContentView(R.layout.activity_main_activiy)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val btnSearch : ImageButton = findViewById(R.id.btn_search)
        val btnSettings : ImageButton = findViewById(R.id.btn_settings)

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
            val intent : Intent = Intent(this, SignInActivity::class.java)
            //val intent : Intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }
}
