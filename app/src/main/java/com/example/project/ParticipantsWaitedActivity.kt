package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.arrayAdapterCustom.ArrayAdapterCustomUsers
import com.example.arrayAdapterCustom.ArrayAdapterCustomUsersWaiting
import com.example.dialog.AlertDialogCustom
import com.example.events.Event
import com.example.user.User
import com.example.user.UserWithKey
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_event_info_jojo.*

class ParticipantsWaitedActivity : AppCompatActivity() {
    private lateinit var keyEvent : String
    private lateinit var listView : ListView
    private lateinit var noResults : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participants_waited)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Users waiting acceptation"

        listView = findViewById<ListView>(R.id.listViewUsers)
        noResults = findViewById(R.id.noResultsLayout)

        val infos : Bundle? = intent.extras
        keyEvent = infos?.getString("key").toString()

        createUsersWaiting(this, keyEvent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun createUsersWaiting(context: Context, key : String?){
        val refLinker = FirebaseDatabase.getInstance().getReference("linker/$keyEvent")
        refLinker.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val date = p0.value as String
                val ref = FirebaseDatabase.getInstance().getReference("events/$date/$key/participants")
                val participants : ArrayList<String?> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val data = dataSnapshot.children //Children = each event
                        data.forEach {
                            if(it.child("status").value == "waiting"){
                                participants.add(it.key)
                            }
                        }
                        searchUser(context, participants)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
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
                    val adapter = ArrayAdapterCustomUsersWaiting(
                        context,
                        R.layout.list_item_user_waiting,
                        keyEvent,
                        usersWaiting,
                        supportFragmentManager
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
                            createUsersWaiting(context, keyEvent)
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
