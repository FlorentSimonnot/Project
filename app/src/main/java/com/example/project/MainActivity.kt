package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.events.Event
import com.example.events.Privacy
import com.example.events.Sport
import com.example.session.SessionUser
import com.example.user.User
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener


class MainActivity : AppCompatActivity() {
    private lateinit var textMessage: TextView
    /*180719*/
    var session : SessionUser = SessionUser()
    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
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

                val checkAccountIntent = Intent(this, UserInfoActivity::class.java)
                startActivity(checkAccountIntent)
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
            val test = findViewById<TextView>(R.id.test)
            //currentUser.writeInfoUser(session.getIdFromUser(), findViewById(R.id.whatsUp), "name", "What's up")
        } else {
            val logInIntent = Intent(this, LoginActivity::class.java)
            //Flags allow to block come back
            logInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(logInIntent)
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        /* ----------- BUTTON ACTIONS -------------*/
        val btnSearch : ImageButton = findViewById(R.id.btn_search)
        val btnSettings : ImageButton = findViewById(R.id.btn_settings)
        //val btnSignOut : Button = findViewById(R.id.btnSignOut)

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

        /*btnSignOut.setOnClickListener {
            session.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }*/

        /* ----------- EVENTS LIST ---------------*/
        val events = listOf<Event>(
            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            ),            Event(
                "five a lognes",
                Sport.FOOTBALL,
                "22/01/2020",
                "Lognes",
                10,
                "que des pros svp",
                Privacy.PUBLIC
            )

        )

        val listView = findViewById<ListView>(R.id.events_listview)
        val adapter = ArrayAdapter<Event>(
            this,
            android.R.layout.simple_list_item_1,
            events
        )
        listView.adapter = adapter

        val createEvent = findViewById<Button>(R.id.create_event)
        createEvent.setOnClickListener {
            val createEventIntent = Intent(this, CreateEventActivity::class.java)
            startActivity(createEventIntent)
        }

    }

    override fun onStart() {
        super.onStart()
    }


}
