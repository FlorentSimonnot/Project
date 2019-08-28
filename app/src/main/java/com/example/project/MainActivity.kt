package com.example.project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.dateCustom.DateCustom
import com.example.dateCustom.TimeCustom
import com.example.dialog.AlertDialogWithRatingBar
import com.example.events.Event
import com.example.menu.MenuCustom
import com.example.notification.MyFirebaseMessagingService
import com.example.session.SessionUser
import com.example.user.User
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_forgot_password.view.*


class MainActivity : AppCompatActivity() {
    /*180719*/
    var session : SessionUser = SessionUser()
    lateinit var textViewMessageCount : TextView

    /*private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
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
    }*/

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
        val menu = MenuCustom(this, navView, this@MainActivity)
        navView.setOnNavigationItemSelectedListener(menu.onNavigationItemSelectedListener)

        //Actualise badges when changes
        FirebaseDatabase.getInstance().getReference("discussions").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                menu.setBadges()
            }

        })

        /* ----------- BUTTON ACTIONS -------------*/
        val btnSearch : ImageButton = findViewById(R.id.btn_search)
        val btnSettings : ImageButton = findViewById(R.id.btn_settings)

        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchBarActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


        /* ----------- SHOW EVENTS ---------------*/
        val listView = findViewById<ListView>(R.id.events_listview)
        showAllEvents(this, listView)

        val createEvent = findViewById<Button>(R.id.create_event)
        createEvent.setOnClickListener {
            val createEventIntent = Intent(this, CreateEventActivity::class.java)
            startActivity(createEventIntent)
        }

    }

    /**
     * Get All of events from Database and show them in listView
     */
    private fun showAllEvents(activity: Activity,  listView : ListView){
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                val events : ArrayList<Event> = ArrayList()
                data.forEach {
                    val event = it.getValue(Event::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if(event != null){
                        /*there is a pb in this block*/
                        val date = DateCustom(event.date)
                        val time = TimeCustom(event.time)
                        if(date.isEqual(DateCustom("01/01/1971").getCurrentDate())){
                            if(time.isAfter(TimeCustom("01:01").getCurrentTime())){
                                events.add(event)
                            }
                        }
                        else if(date.isAfter(DateCustom("01/01/1971").getCurrentDate())){
                            events.add(event)
                        }
                    }
                }
                val adapter = ArrayAdapterCustom(activity, R.layout.my_list, events)
                listView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchFinishEvent(context: Context, events: ArrayList<String>){

        val eventsFinish = ArrayList<String>()
        events.forEach {
            val ref = FirebaseDatabase.getInstance().getReference("events/$it")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.child("finish").value == true){
                        eventsFinish.add(it)
                    }

                    val alert = AlertDialogWithRatingBar(context, "You have to rate events")
                    alert.create()
                    alert.show()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}
