package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener

class MessagerieActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var newMessage : ImageButton

    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                val checkAccountIntent = Intent(this, ActivityInfoUser::class.java)
                startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                val checkAccountIntent = Intent(this, MessagerieActivity::class.java)
                startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagerie)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        newMessage = findViewById(R.id.btn_new_message)
        newMessage.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_new_message -> {
                val intent = Intent(this, ListUserSendMessage::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
        }
    }
}
