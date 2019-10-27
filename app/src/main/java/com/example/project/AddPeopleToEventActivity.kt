package com.example.project

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.arrayAdapterCustom.ArrayAdapterCustomUsers
import com.example.arrayAdapterCustom.ArrayAdapterInviteFriends
import com.example.events.Event
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.example.user.UserWithKeyAndStatus
import com.google.firebase.database.*
import java.sql.DriverManager.println

class AddPeopleToEventActivity : AppCompatActivity() {
    val session = SessionUser(this)
    private lateinit var keyEvent : String
    private lateinit var listView : ListView
    private lateinit var noResults : RelativeLayout
    private lateinit var noResultsText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_people_to_event)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.event_info_invite_friends)

        listView = findViewById(R.id.listViewFriends)
        noResults = findViewById(R.id.noResultsLayout)
        noResultsText = findViewById(R.id.noResultsText)

        val infos : Bundle? = intent.extras
        keyEvent = infos?.getString("key").toString()

        FirebaseDatabase.getInstance().getReference("linker/$keyEvent").addValueEventListener(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    FirebaseDatabase.getInstance().getReference("events/${p0.value as String}/$keyEvent").addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            val event = p0.getValue(Event::class.java) as Event
                            if(event.participants.size < event.nb_people){
                                createUsersParticipe(this@AddPeopleToEventActivity, keyEvent)
                            }else{
                                noResultsText.text = getString(R.string.event_info_cannot_invite)
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {}

                    })
                }
            }

        )
    }

    private fun createUsersParticipe(context: Context, key: String){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${session.getIdFromUser()}")
        val participants : ArrayList<String?> = ArrayList()
        val status : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val refEvent = FirebaseDatabase.getInstance().getReference("events/$key/participants")
                refEvent.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        val friends = dataSnapshot.children //friend
                        val dataParticipants = p0.children

                        val keysParticipants = ArrayList<String>()
                        dataParticipants.forEach {
                            keysParticipants.add(it.key!!)
                        }

                        friends.forEach {
                            //Friend is already in participation
                            if(keysParticipants.contains(it.key!!)){
                                val refP = FirebaseDatabase.getInstance().getReference("events/$key/participants/${it.key}")
                                refP.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        //Nothing
                                    }

                                    override fun onDataChange(p2: DataSnapshot) {
                                        val value = p2.child("status").value
                                        if(value == "invitation"){
                                            participants.add(it.key)
                                            status.add("already")
                                        }
                                    }
                                })
                            }
                            else{
                                participants.add(it.key)
                                status.add("no")
                            }
                        }
                        searchUser(context, participants, status)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchUser(context: Context, participants : ArrayList<String?>, status : ArrayList<String?>){
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each user
                val usersWaiting : ArrayList<UserWithKeyAndStatus> = ArrayList()
                data.forEachIndexed{ index, it ->
                    val user = it.getValue(User::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if(user != null){
                        if(participants.contains(it.key)){
                            val i = participants.indexOf(it.key)
                            usersWaiting.add(UserWithKeyAndStatus(user, it.key, status[i]!!))
                        }
                    }
                }
                if(usersWaiting.size > 0){
                    noResults.visibility = View.GONE
                    listView.visibility = View.VISIBLE
                    val adapter = ArrayAdapterInviteFriends(
                        context,
                        R.layout.list_item_user_invitation_to_event,
                        usersWaiting,
                        keyEvent
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
                            createUsersParticipe(context, keyEvent)
                        }

                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            //
                        }

                        override fun onChildRemoved(p0: DataSnapshot) {
                            createUsersParticipe(context, keyEvent)
                        }
                    })
                }
                else{
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

}
