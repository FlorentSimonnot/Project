package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.view.forEach
import com.example.arrayAdapterCustom.ArrayAdapterFriends
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*

class FriendsActivity : AppCompatActivity() {
    var session = SessionUser(this)
    private lateinit var tab : TabLayout
    private lateinit var listView : ListView
    private lateinit var noResults: TextView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Your friends"

        tab = findViewById(R.id.tab)
        tab.forEach {
            val tabItem : TextView = LayoutInflater.from(this).inflate(R.layout.tab_tem_layout, null) as TextView
            tab.getTabAt(it.id)?.customView = tabItem
        }
        session.setFriendsOnTabItem(tab)
        session.setInvitationsOnTabItem(tab)

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
                        friendsList(applicationContext)
                        supportActionBar?.title = "Your friends"
                    }
                    1 ->{
                        waitingList(applicationContext)
                        supportActionBar?.title = "Your Invitations"
                    }
                }
            }

        })

        noResults = findViewById(R.id.noResults)
        listView = findViewById<ListView>(R.id.listViewUsers)
        friendsList(this)

    }

    private fun friendsList(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.session.getIdFromUser()}")
        val friends : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.child("status").value == "friend"){
                        friends.add(it.key)
                    }
                }
                searchUser(context, friends, "friend")
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun waitingList(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.session.getIdFromUser()}")
        val waiting : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.child("status").value == "waiting"){
                        waiting.add(it.key)
                    }
                }
                searchUser(context, waiting, "waiting")
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchUser(context: Context, participants : ArrayList<String?>, action : String) {
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each user
                val users: ArrayList<UserWithKey> = ArrayList()
                data.forEach {
                    val user = it.getValue(User::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if (user != null) {
                        if (participants.contains(it.key)) {
                            users.add(UserWithKey(user, it.key))
                        }
                    }
                }
                if (users.size > 0) {
                    noResults.visibility = View.GONE
                    listView.visibility = View.VISIBLE
                    listView.clearChoices()
                    var layout = 0
                    when(action){
                        "friend" -> {
                            layout = R.layout.list_item_user_confirmed
                        }
                        "waiting" -> {
                            layout = R.layout.list_item_user_waiting
                        }
                    }
                    val adapter = ArrayAdapterFriends(
                        context,
                        layout,
                        users,
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
                                "friend" -> {
                                    friendsList(context)
                                }
                                "waiting" -> {
                                    waitingList(context)
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

}
