package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.discussion.LatestMessageAdapter
import com.example.menu.MenuCustom
import com.example.messages.DiscussionViewLastMessage
import com.example.messages.Message
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
    private val keysChat = ArrayList<String>()
    private var latestDiscussion = ArrayList<DiscussionViewLastMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagerie)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val menu = MenuCustom(this, navView, this@MessagerieActivity)
        navView.setOnNavigationItemSelectedListener(menu.onNavigationItemSelectedListener)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        recyclerView.layoutManager = llm


        newMessage = findViewById(R.id.btn_new_message)
        newMessage.setOnClickListener(this)

        //searchDiscussion(this)

        //Actualise badges when changes and messages
        FirebaseDatabase.getInstance().getReference("discussions").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                menu.setBadges()
                keysChat.clear()
                latestDiscussion.clear()
                searchDiscussion(this@MessagerieActivity)
            }

        })
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

                    //Collect all messages
                    data.forEach {
                        val message : Message = it.getValue(Message::class.java) as Message
                        messages.add(message)
                    }
                    val sortedList = ArrayList(messages.sortedWith(compareBy({it.date}, {it.time})).toList())
                    if(sortedList.size > 0) {
                        latestDiscussion.add(DiscussionViewLastMessage(
                            it,
                            sortedList[sortedList.size - 1])
                        )
                    }
                    if(index == keysChat.size - 1) {
                        if (latestDiscussion.size > 0) {
                            latestDiscussion = ArrayList(latestDiscussion.sortedWith(compareBy({it.lastMessage.date}, {it.lastMessage.time})).toList())
                            latestDiscussion.reverse()
                            adapter = LatestMessageAdapter(context, R.layout.list_item_last_message, latestDiscussion, this@MessagerieActivity)
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
            if(latestDiscussion[position].lastMessage.sender == session.getIdFromUser()){
                intent.putExtra("keyUser", latestDiscussion[position].lastMessage.addressee)
            }else {
                intent.putExtra("keyUser", latestDiscussion[position].lastMessage.sender)
            }
            intent.putExtra("keyChat", latestDiscussion[position].keyChat)
            this@MessagerieActivity.startActivity(intent)
        }

    }


}
