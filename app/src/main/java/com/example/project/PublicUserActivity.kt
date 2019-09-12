package com.example.project

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.forEach
import com.example.arrayAdapterCustom.ArrayAdapterEvents
import com.example.dialog.DialogProfilView
import com.example.events.Event
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_next_sign_in_jojo.*
import java.lang.StringBuilder

class PublicUserActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var user: UserWithKey
    var session = SessionUser(this)
    lateinit var tab : TabLayout
    lateinit var userKey : String
    private var joinedView = false
    private lateinit var listView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_user)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"

        val infos : Bundle? = intent.extras
        userKey = infos?.getString("user").toString()


        val identityTextView = findViewById<TextView>(R.id.identity)
        val placeTextView = findViewById<TextView>(R.id.place)
        val addFriendButton = findViewById<Button>(R.id.addFriendButton)
        val removeFriendButton = findViewById<Button>(R.id.removeFriendButton)
        val cancelFriendButton = findViewById<Button>(R.id.cancelFriendButton)
        val aboutMoreButton = findViewById<Button>(R.id.aboutMoreButton)
        listView = findViewById(R.id.listViewEvents)
        tab = findViewById(R.id.tab)


        tab.forEach {
            val tabItem : TextView = LayoutInflater.from(this).inflate(R.layout.tab_tem_layout, null) as TextView
            tab.getTabAt(it.id)?.customView = tabItem
        }
        User().setEventsOnTabItem(tab, userKey)
        User().setEventsJoinedOnTabItem(tab, userKey)

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
                        eventsList(this@PublicUserActivity)
                    }
                    1 ->{
                        eventsJoinedList(this@PublicUserActivity)
                    }
                }
            }

        })


        if(joinedView){
            val item = tab.getTabAt(1)
            item?.isSelected
            eventsJoinedList(this)
        }
        else {
            eventsList(this)
        }

        SessionUser(this).writeInfoUser(this, userKey, identityTextView, "identity")
        SessionUser(this).writeInfoUser(this, userKey, placeTextView, "city")
        SessionUser(this).writeInfoUser(this, userKey, aboutMoreButton, "firstNameButton")

        //Show the good button.
        FirebaseDatabase.getInstance().getReference("friends/$userKey/${session.getIdFromUser()}").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {session.setButtonFriend(addFriendButton, removeFriendButton, cancelFriendButton, userKey)}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                session.setButtonFriend(addFriendButton, removeFriendButton, cancelFriendButton, userKey)
            }

            override fun onChildRemoved(p0: DataSnapshot) {session.setButtonFriend(addFriendButton, removeFriendButton, cancelFriendButton, userKey)}

        })

        addFriendButton.setOnClickListener(this)
        removeFriendButton.setOnClickListener(this)
        cancelFriendButton.setOnClickListener(this)
        aboutMoreButton.setOnClickListener(this)
    }

    private fun eventsList(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("users/$userKey/eventsCreated")
        val events : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    events.add(it.key)
                }
                searchEvents(context, events, "created")
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun eventsJoinedList(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("users/$userKey/eventsJoined")
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
                if (events.size > 0) {
                    //noResults.visibility = View.GONE
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
                    //noResults.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onClick(p0: View?) {
        val userWithKey = UserWithKey(User(), userKey)
        when(p0?.id){
            R.id.addFriendButton -> {
                userWithKey.addFriend(session)
            }
            R.id.removeFriendButton -> {
                userWithKey.deleteFriend(this@PublicUserActivity, session)
            }
            R.id.cancelFriendButton -> {
                userWithKey.cancelFriendInvitation(this@PublicUserActivity, session)
            }
            R.id.aboutMoreButton -> {
                val dialog = DialogProfilView(this@PublicUserActivity, R.layout.dialog_profil_layout, userKey)
                dialog.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

