package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.RelativeLayout
import com.example.arrayAdapterCustom.ArrayAdapterFriends
import com.example.arrayAdapterCustom.ArrayAdapterUsersSendMessage
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.firebase.database.*

class ListUserSendMessage : AppCompatActivity() {
    val session = SessionUser(this)
    private lateinit var listView : ListView
    private lateinit var noResults : RelativeLayout
    private lateinit var searchBar : EditText
    val friends : ArrayList<String?> = ArrayList()
    val keyChats : ArrayList<String?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user_send_message)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Choose recipient"

        listView = findViewById(R.id.listViewUsers)
        noResults = findViewById(R.id.noResultsLayout)
        searchBar = findViewById(R.id.searchUser)

        friendsList(this, "")

        searchBar.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                friends.clear()
                keyChats.clear()
                //listView.removeAllViews()
                friendsList(this@ListUserSendMessage, p0?.toString()!!)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }

    private fun friendsList(context: Context, stringSearch : String){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.session.getIdFromUser()}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.child("status").value == "friend"){
                        if(it.hasChild("keyChat")) {
                            friends.add(it.key)
                            keyChats.add(it.child("keyChat").value as String)
                        }
                    }
                }
                searchUser(context, friends, keyChats, stringSearch)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchUser(context: Context, participants : ArrayList<String?>, keyChats : ArrayList<String?>, stringSearch: String) {
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
                            if(stringSearch.isNotEmpty()) {
                                if (user.firstName.contains(stringSearch) || user.name.contains(stringSearch)) {
                                    users.add(UserWithKey(user, it.key))
                                }
                            }
                            else{
                                users.add(UserWithKey(user, it.key))
                            }
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
                            friendsList(context, "")
                        }

                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            //
                        }

                        override fun onChildRemoved(p0: DataSnapshot) {
                            friendsList(context, "")
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
