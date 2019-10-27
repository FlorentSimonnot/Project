package com.example.project

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
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
import com.example.utils.Pair
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*

class EventActivity : AppCompatActivity(), View.OnClickListener, NumberPicker.OnValueChangeListener {
    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        if(p0?.maxValue == 2){
            filterValue = p0?.value
            eventsList(this)
        }
    }

    var session = SessionUser(this)
    private lateinit var tab : TabLayout
    private lateinit var listView : ListView
    private lateinit var noResults: TextView
    private lateinit var valuesFilter: Array<String>
    private var filterValue = 0
    private var joinedView = false
    private val eventsKey : ArrayList<String?> = ArrayList()
    private var events : ArrayList<Event> = ArrayList()
    private val list = ArrayList<Pair>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        valuesFilter = arrayOf(getString(R.string.events_filter_all_events), getString(R.string.events_filter_only_finished), getString(R.string.events_filter_only_next))
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
                        eventsKey.clear()
                        events.clear()
                        list.clear()
                        eventsList(this@EventActivity)
                        supportActionBar?.title = resources.getString(R.string.events_created_title)
                    }
                    1 ->{
                        filterValue = 0
                        eventsKey.clear()
                        events.clear()
                        list.clear()
                        eventsJoinedList(this@EventActivity)
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
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    eventsKey.add(it.key)
                }
                if(eventsKey.size > 0)
                    searchEventFromDate(context, eventsKey,"created", filterValue)
                else{
                    noResults.visibility = View.VISIBLE
                    listView.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun eventsJoinedList(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.session.getIdFromUser()}/eventsJoined")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    eventsKey.add(it.key)
                }
                if(eventsKey.size > 0)
                    searchEventFromDate(context, eventsKey,"joined", 0)
                else{
                    noResults.visibility = View.VISIBLE
                    listView.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchEventFromDate(context: Context, events : ArrayList<String?>, action : String, filter : Int){
        events.forEachIndexed { i, it ->

            FirebaseDatabase.getInstance().getReference("linker/${it}").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    list.add(Pair(it!!, p0.value!! as String))

                    if(i == events.size - 1){
                        searchEvents(context, list, action, filter)
                    }
                }

            })
        }
    }

    private fun searchEvents(context: Context, participants : ArrayList<Pair>,  action : String, filter : Int) {
        participants.forEachIndexed{ i, it ->
            val ref = FirebaseDatabase.getInstance().getReference("events/${it.second}/${it.first}")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val event = dataSnapshot.getValue(Event::class.java)!!
                    events.add(event)

                    if(i == participants.size - 1){
                        noResults.visibility = View.GONE
                        listView.visibility = View.VISIBLE
                        listView.clearChoices()
                        events = ArrayList(events.sortedWith(compareBy{ it.date }).reversed())
                        val layout = R.layout.my_list_mini
                        val adapter = ArrayAdapterEvents(
                            context,
                            layout,
                            events,
                            action
                        )
                        adapter.notifyDataSetChanged()
                        listView.adapter = adapter
                        val dref = FirebaseDatabase.getInstance().reference
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
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(p0: View?) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_filters -> {
                val privacyPicker = StringPickerCustom(this, 0, 2, getString(R.string.events_filter_title), getString(R.string.events_filter_message), valuesFilter)
                privacyPicker.setValueChangeListener(this)
                privacyPicker.show(supportFragmentManager, "Filter picker")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
