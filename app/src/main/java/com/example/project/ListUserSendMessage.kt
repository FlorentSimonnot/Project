package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import com.example.arrayAdapterCustom.ArrayAdapterFriends
import com.example.arrayAdapterCustom.ArrayAdapterUsersSendMessage
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.firebase.database.*

class ListUserSendMessage : AppCompatActivity() {
    val session = SessionUser()
    private lateinit var listView : ListView
    private lateinit var noResults : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user_send_message)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Choose recipient"

        listView = findViewById(R.id.listViewUsers)
        noResults = findViewById(R.id.noResultsLayout)

        friendsList(this)
    }

    private fun friendsList(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.session.getIdFromUser()}")
        val friends : ArrayList<String?> = ArrayList()
        val keyChats : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.child("status").value == "friend"){
                        friends.add(it.key)
                        keyChats.add(it.child("keyChat").value as String)
                    }
                }
                searchUser(context, friends, keyChats)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchUser(context: Context, participants : ArrayList<String?>, keyChats : ArrayList<String?>) {
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
                    val adapter = ArrayAdapterUsersSendMessage(
                        context,
                        R.layout.list_item_user,
                        users,
                        keyChats
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
                            friendsList(context)
                        }

                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            //
                        }

                        override fun onChildRemoved(p0: DataSnapshot) {
                            friendsList(context)
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
        val intent = Intent(this, MessagerieActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ListUserSendMessage::finish
        startActivity(intent)
        return true
    }
}
