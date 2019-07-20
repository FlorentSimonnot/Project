package com.example.project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.events.Event
import com.example.events.Privacy
import com.example.events.Sport
import com.example.session.SessionUser
import com.example.user.User
import com.example.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private lateinit var textMessage: TextView
    /*180719*/
    var session : SessionUser = SessionUser()
    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                val checkAccountIntent = Intent(this, UserInfoActivity::class.java)
                startActivity(checkAccountIntent)
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
        setContentView(R.layout.activity_main_activity)

        if (!session.isLogin()) {
            val logInIntent = Intent(this, LoginActivity::class.java)
            //Flags allow to block come back
            logInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(logInIntent)
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //textMessage = findViewById(R.id.message)
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

        /* ----------- SHOW EVENTS ---------------*/
        val listView = findViewById<ListView>(R.id.events_listview)
        val relativeLayout = findViewById<RelativeLayout>(R.id.layoutEventsView)
        showAllEvents(this, relativeLayout, listView)

        val createEvent = findViewById<Button>(R.id.create_event)
        createEvent.setOnClickListener {
            val createEventIntent = Intent(this, CreateEventActivity::class.java)
            startActivity(createEventIntent)
        }

    }

    override fun onStart() {
        super.onStart()
    }

    /**
     * Get All of events from Database and show them in listView
     */
    private fun showAllEvents(activity: Activity, layout : RelativeLayout,  listView : ListView){
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                var events : ArrayList<Event> = ArrayList()
                data.forEach {
                    val event = it.getValue(Event::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if(event != null){
                        events.add(event)
                    }
                }
                val adapter = ArrayAdapterCustom(activity, R.layout.my_list, events)
                listView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


}
