package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.forEach
import com.example.arrayAdapterCustom.ArrayAdapterEvents
import com.example.arrayAdapterCustom.ArrayAdapterFriends
import com.example.events.Event
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*

class EventActivity : AppCompatActivity() {
    var session = SessionUser()
    private lateinit var tab : TabLayout
    private lateinit var listView : ListView
    private lateinit var noResults: TextView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        tab = findViewById(R.id.tab)
        tab.forEach {
            val tabItem : TextView = LayoutInflater.from(this).inflate(R.layout.tab_tem_layout, null) as TextView
            tab.getTabAt(it.id)?.customView = tabItem
        }
        session.setEventsOnTabItem(tab)
        session.setEventsJoinedOnTabItem(tab)

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
                //Nothing
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                //Nothing
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when(p0?.position){
                    0 -> {
                        eventsList(applicationContext)
                        supportActionBar?.title = "Events created"
                    }
                    1 ->{
                       eventsJoinedList(applicationContext)
                        supportActionBar?.title = "Events joined"
                    }
                }
            }

        })

        noResults = findViewById(R.id.noResults)
        listView = findViewById(R.id.listViewEvents)

        eventsList(this)
    }

    private fun eventsList(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.session.getIdFromUser()}/eventsCreated")
        val events : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    events.add(it.key)
                }
                println("EVENTS KEY : ${events}")
                searchEvents(context, events, "created")
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun eventsJoinedList(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.session.getIdFromUser()}/eventsJoined")
        val events : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    events.add(it.key)
                }
                searchEvents(context, events, "joined")
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchEvents(context: Context, participants : ArrayList<String?>, action : String) {
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                val events: ArrayList<Event> = ArrayList()
                data.forEach {
                    val event = it.getValue(Event::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if (event != null) {
                        if (participants.contains(it.key)) {
                            events.add(event)
                        }
                    }
                }
                println("EVENTS : ${events}")
                if (events.size > 0) {
                    noResults.visibility = View.GONE
                    listView.visibility = View.VISIBLE
                    listView.clearChoices()
                    var layout = 0
                    when(action){
                        "created" -> {
                            layout = R.layout.my_list
                        }
                        "joined" -> {
                            layout = R.layout.my_list
                        }
                    }
                    val adapter = ArrayAdapterEvents(
                        context,
                        layout,
                        events,
                        action
                    )
                    adapter.notifyDataSetChanged()
                    listView.adapter = adapter
                    val dref = FirebaseDatabase.getInstance().reference;
                    dref.addChildEventListener(object : ChildEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            //
                        }

                        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                            //
                        }

                        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                            when(action){
                                "created" -> {
                                    eventsList(context)
                                }
                                "joined" -> {
                                    eventsJoinedList(context)
                                }

                            }
                            session.setFriendsOnTabItem(tab)
                            session.setInvitationsOnTabItem(tab)
                        }

                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            //
                        }

                        override fun onChildRemoved(p0: DataSnapshot) {
                            //
                        }
                    })
                } else {
                    listView.visibility = View.GONE
                    noResults.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val intent = Intent(this, ActivityInfoUser::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        EventInfoJojoActivity::finish
        startActivity(intent)
        return true
    }
}
