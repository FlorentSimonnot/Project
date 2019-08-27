package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messages.LatestMessageAdapter
import com.example.messages.Message
import com.example.messages.MessageAdapter
import com.example.session.SessionUser
import com.example.utils.ItemSupportClick
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessagerieActivity : AppCompatActivity(), View.OnClickListener, LatestMessageAdapter.OnItemListener {

    private lateinit var newMessage : ImageButton
    private val session = SessionUser()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : LatestMessageAdapter
    private lateinit var itemSupportClick: ItemSupportClick
    private val latestMessages = ArrayList<Message>()
    private val keysChat = ArrayList<String>()

    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                val checkAccountIntent = Intent(this, ActivityInfoUser::class.java)
                startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                val checkAccountIntent = Intent(this, MessagerieActivity::class.java)
                startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagerie)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        recyclerView.layoutManager = llm


        newMessage = findViewById(R.id.btn_new_message)
        newMessage.setOnClickListener(this)

        searchDiscussion(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_new_message -> {
                val intent = Intent(this, ListUserSendMessage::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
        }
    }

    private fun searchDiscussion(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("friends/${session.getIdFromUser()}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children
                data.forEach {
                    if(it.hasChild("keyChat")){
                        val key = it.child("keyChat").value as String
                        keysChat.add(key)
                    }
                }
                searchLatestMessages(context, keysChat)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchLatestMessages(context: Context, keysChat : ArrayList<String>){
        keysChat.forEachIndexed {index, it ->
            val ref = FirebaseDatabase.getInstance().getReference("discussions/$it/messages")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.children //Children = each messages
                    val messages : ArrayList<Message> = ArrayList()
                    data.forEach {
                        val message : Message = it.getValue(Message::class.java) as Message
                        messages.add(message)
                    }
                    val sortedList = ArrayList(messages.sortedWith(compareBy({it.date}, {it.time})).toList())
                    if(sortedList.size > 0) {
                        latestMessages.add(sortedList[sortedList.size - 1])
                    }
                    if(index == keysChat.size - 1) {
                        if (latestMessages.size > 0) {
                            adapter = LatestMessageAdapter(context, R.layout.list_item_last_message, latestMessages, this@MessagerieActivity)
                            recyclerView.adapter = adapter
                            if (adapter.itemCount > 0) {
                                val position = recyclerView.adapter!!.itemCount
                                recyclerView.smoothScrollToPosition(position - 1)
                            }
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    override fun onClick(position: Int) {
        if(position < keysChat.size) {
            val intent = Intent(this@MessagerieActivity, Dialog::class.java)
            if(latestMessages[position].sender == session.getIdFromUser()){
                intent.putExtra("keyUser", latestMessages[position].addressee)
            }else {
                intent.putExtra("keyUser", latestMessages[position].sender)
            }
            intent.putExtra("keyChat", keysChat[position])
            this@MessagerieActivity.startActivity(intent)
        }

    }


}
