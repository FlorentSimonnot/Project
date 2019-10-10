package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.arrayAdapterCustom.ArrayAdapterCustomUsers
import com.example.dialog.AlertCustomTitle
import com.example.dialog.AlertCustomWithEditText
import com.example.events.Event
import com.example.messages.ChatEvent
import com.example.user.User
import com.example.user.UserWithKey
import com.google.firebase.database.*
import com.google.firebase.database.core.utilities.Utilities
import android.view.ViewGroup
import com.example.arrayAdapterCustom.ArrayAdapterParticipantsChatEvent


class GroupInformationEventDiscussion : AppCompatActivity(), View.OnClickListener {
    private lateinit var keyChat : String
    private lateinit var changeColorBackground : RelativeLayout
    private lateinit var listView : ListView
    private lateinit var titleListView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_information_event_discussion)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val infos : Bundle? = intent.extras
        keyChat = infos?.getString("keyChat").toString()

        changeColorBackground = findViewById(R.id.l1)
        changeColorBackground.setOnClickListener(this)
        listView = findViewById(R.id.listView)
        titleListView = findViewById(R.id.titleParticipant)

        Event().writeInfoEvent(this, keyChat, titleListView, "participants")

        FirebaseDatabase.getInstance().getReference("chatEvent/$keyChat/parameters").addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    ChatEvent(keyChat).setTitleDiscussion(this@GroupInformationEventDiscussion, supportActionBar!!)
                }

            }
        )

        createUsersParticipe(this, keyChat)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_edit -> {
                val dialog = AlertCustomTitle(
                    this,
                    R.layout.layout_dialog_with_textview,
                    getString(R.string.modify_event_name_alert_title),
                    getString(R.string.modify_event_name_alert_message),
                    keyChat,
                    "name"
                )
                dialog.show(supportFragmentManager, "Title")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.l1 -> {
                startActivity(Intent(this@GroupInformationEventDiscussion, ChooseBackgroundColor::class.java).putExtra("keyChat", keyChat))
            }
        }
    }

    private fun createUsersParticipe(context: Context, key: String){
        val ref = FirebaseDatabase.getInstance().getReference("events/$key/participants")
        val participants : ArrayList<String?> = ArrayList()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    if(it.child("status").value == "confirmed" || it.child("status").value == "creator"){
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

                if(usersWaiting.size > 0){
                    val adapter = ArrayAdapterParticipantsChatEvent(
                        context,
                        R.layout.list_item_participant_chat_event,
                        keyChat,
                        usersWaiting,
                        "confirm",
                        supportFragmentManager,
                        6
                    )
                    adapter.notifyDataSetChanged()
                    listView.adapter = adapter
                    val params = listView.layoutParams
                    var totalHeight = 0
                    for (i in 0 until adapter.count) {
                        val listItem = adapter.getView(i, null, listView)
                        listItem.measure(0, 0)
                        totalHeight += listItem.measuredHeight
                    }
                    params.height = totalHeight + listView.dividerHeight * (adapter.count - 1)
                    listView.layoutParams = params
                    listView.requestLayout()
                    val dref = FirebaseDatabase.getInstance().reference
                    dref.addChildEventListener(object : ChildEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            //
                        }

                        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                            //
                        }

                        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                            createUsersParticipe(context, keyChat)
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
