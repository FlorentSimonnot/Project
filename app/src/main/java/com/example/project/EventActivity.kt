package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.core.view.get
import com.example.arrayAdapterCustom.ArrayAdapterEvents
import com.example.arrayAdapterCustom.ArrayAdapterFriends
import com.example.events.Event
import com.example.picker.StringPickerCustom
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*

class EventActivity : AppCompatActivity(), View.OnClickListener, NumberPicker.OnValueChangeListener {
    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        if(p0?.maxValue == 2){
            //filter.text = valuesFilter[p0?.value]
            filterValue = p0?.value
            eventsList(this)
        }
    }

    var session = SessionUser(this)
    private lateinit var tab : TabLayout
    private lateinit var listView : ListView
    private lateinit var noResults: TextView
    private var valuesFilter = arrayOf("All events", "Only finished events", "Only next events")
    private var filterValue = 0
    private var joinedView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        val infos : Bundle? = intent.extras
        joinedView = infos?.getString("joinedView").toString().toBoolean()

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = resources.getString(R.string.events_created_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
                        filterValue = 0
                        //filter.text = valuesFilter[filterValue]
                        eventsList(applicationContext)
                        supportActionBar?.title = resources.getString(R.string.events_created_title)
                    }
                    1 ->{
                        filterValue = 0
                        //filter.text = valuesFilter[filterValue]
                        eventsJoinedList(applicationContext)
                        supportActionBar?.title = resources.getString(R.string.events_joined_title)
                    }
                }
            }

        })

        noResults = findViewById(R.id.noResults)
        listView = findViewById(R.id.listViewEvents)

        if(joinedView){
            val item = tab.getTabAt(1)
            item?.isSelected
            supportActionBar?.title = resources.getString(R.string.events_joined_title)
            eventsJoinedList(this)
        }
        else {
            eventsList(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_events, menu)
        return true
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
                searchEvents(context, events, "created", filterValue)
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
                searchEvents(context, events, "joined", 0)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchEvents(context: Context, participants : ArrayList<String?>, action : String, filter : Int) {
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
                            when(filter){
                                0 -> events.add(event)
                                1 -> {
                                    if(event.finish){
                                        events.add(event)
                                    }
                                }
                                2 -> {
                                    if(!event.finish){
                                        events.add(event)
                                    }
                                }
                            }
                        }
                    }
                }
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
        return true
    }

    override fun onClick(p0: View?) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_filters -> {
                val privacyPicker = StringPickerCustom(0, 2, "Filter", "select categorie of event", valuesFilter)
                privacyPicker.setValueChangeListener(this)
                privacyPicker.show(supportFragmentManager, "Filter picker")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
