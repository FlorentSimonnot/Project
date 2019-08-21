package com.example.project

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.arrayAdapterCustom.ArrayAdapterCustomUsers
import com.example.arrayAdapterCustom.ArrayAdapterInviteFriends
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.firebase.database.*
import java.sql.DriverManager.println

class AddPeopleToEventActivity : AppCompatActivity() {
    val session = SessionUser()
    private lateinit var keyEvent : String
    private lateinit var listView : ListView
    private lateinit var noResults : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_people_to_event)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Invite your friends to your event"

        listView = findViewById(R.id.listViewFriends)
        noResults = findViewById(R.id.noResultsLayout)

        val infos : Bundle? = intent.extras
        keyEvent = infos?.getString("key").toString()

        createUsersParticipe(this, keyEvent)
    }

    private fun createUsersParticipe(context: Context, key: String){
        val ref = FirebaseDatabase.getInstance().getReference("users/${session.getIdFromUser()}/friends")
        val participants : ArrayList<String?> = ArrayList()
        println("BONJOUR !!!")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /*val refEvent = FirebaseDatabase.getInstance().getReference("events/$key/participants")
                refEvent.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        val data = dataSnapshot.children
                        val dataParticipants = p0.children
                        println("PARTICIPANTS   $dataParticipants")
                        val keysParticipants = ArrayList<String>()
                        dataParticipants.forEach {
                            keysParticipants.add(it.key!!)
                        }
                        println("keysPa : $keysParticipants")
                        data.forEach {
                            println("IT : $it")
                            if(keysParticipants.contains(it.key!!)){
                                val index = keysParticipants.indexOf(it.key!!)
                                val elem = keysParticipants[index]
                                println("ELEMN $elem")
                                val refP = FirebaseDatabase.getInstance().getReference("events/$key/participants/$elem")
                                refP.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        //Nothing
                                    }

                                    override fun onDataChange(p2: DataSnapshot) {
                                        val value = p2.value
                                        println("VALUE $value")
                                        participants.add(it.key)
                                    }
                                })
                            }
                        }
                        searchUser(context, participants)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })*/
                val data = dataSnapshot.children
                println("DATA $data")
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchUser(context: Context, participants : ArrayList<String?>){
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each user
                val usersWaiting : ArrayList<UserWithKey> = ArrayList()
                data.forEach {
                    val user = it.getValue(User::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if(user != null){
                        if(participants.contains(it.key)){
                            usersWaiting.add(UserWithKey(user, it.key))
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
                    val dref = FirebaseDatabase.getInstance().reference;
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
                            //
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
}
