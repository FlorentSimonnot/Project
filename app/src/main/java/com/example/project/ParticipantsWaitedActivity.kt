package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.arrayAdapterCustom.ArrayAdapterCustomUsers
import com.example.dialog.AlertDialogCustom
import com.example.events.Event
import com.example.user.User
import com.example.user.UserWithKey
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_event_info_jojo.*

class ParticipantsWaitedActivity : AppCompatActivity() {
    private lateinit var keyEvent : String
    private lateinit var listView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participants_waited)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Users waiting acceptation"

        listView = findViewById<ListView>(R.id.listViewUsers)

        val infos : Bundle? = intent.extras
        keyEvent = infos?.getString("key").toString()

        createUsersWaiting(this, keyEvent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, EventInfoJojoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("key", keyEvent)
        ParticipantsWaitedActivity::finish
        startActivity(intent)
        return true
    }

    private fun createUsersWaiting(context: Context, key : String?){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants")
        val participants : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.value == "waiting"){
                        participants.add(it.key)
                    }
                }
                searchUser(context, participants)
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
                val adapter = ArrayAdapterCustomUsers(context, R.layout.list_item_user_waiting, keyEvent, usersWaiting, "waiting")
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
                        println("ON CHANGEEEEEEE $p0 and $p1")
                        createUsersWaiting(context, keyEvent)
                        //adapter.remove(dataSnapshot.getValue(String::class.java))
                        //adapter.notifyDataSetChanged()
                    }

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        //
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                        //
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}
