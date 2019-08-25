package com.example.project

import android.content.Context
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.dateCustom.DateCustom
import com.example.dateCustom.TimeCustom
import com.example.events.Event
import com.example.messages.Message
import com.example.messages.MessageAdapter
import com.example.session.SessionUser
import com.example.user.User
import com.example.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Dialog : AppCompatActivity(), View.OnClickListener {
    private lateinit var keyUser : String
    private lateinit var buttonSendMessage : ImageButton
    private lateinit var messageEditText: EditText
    private val session = SessionUser()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        keyUser = infos?.getString("keyUser").toString()

        buttonSendMessage = findViewById(R.id.send_message)
        messageEditText = findViewById(R.id.message)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.addItemDecoration(DividerItemDecoration(this, 0))

        buttonSendMessage.setOnClickListener(this)

        SessionUser().writeNameUserDiscussion(this, keyUser, supportActionBar!!)

        searchMessages(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.send_message -> {
                val text = messageEditText.text.toString()
                println("TEXT $text")
                if(text.isNotEmpty()){
                    val message = Message(
                        Utils().generatePassword(100),
                        session.getIdFromUser(),
                        keyUser,
                        text,
                        DateCustom("00/00/0000").getCurrentDate().toString(),
                        TimeCustom("00:00").getCurrentTime().toString()
                    )
                    message.insertMessage(this, messageEditText)
                }
            }
        }
    }

    private fun searchMessages(context: Context){
        val ref = FirebaseDatabase.getInstance().getReference("messages/${session.getIdFromUser()}/$keyUser")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each messages
                val messages : ArrayList<Message> = ArrayList()
                data.forEach {
                    val message : Message = it.getValue(Message::class.java) as Message
                    if(message != null){
                        messages.add(message)
                    }
                }
                searchMessagesAddressee(context, messages)
                //val adapter = ArrayAdapterCustom(activity, R.layout.my_list, events)
                //listView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchMessagesAddressee(context : Context, messages : ArrayList<Message>){
        val ref = FirebaseDatabase.getInstance().getReference("messages/$keyUser/${session.getIdFromUser()}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each messages
                data.forEach {
                    val message : Message = it.getValue(Message::class.java) as Message
                    if(message != null){
                        messages.add(message)
                        println("MESSAGE $message")
                    }
                }
                if(messages.size > 0){
                    val sortedList = messages.sortedWith(compareBy({it.date}, {it.time})).toList()
                    adapter = MessageAdapter(context, R.layout.list_item_message_me, R.layout.list_item_message, ArrayList(sortedList))
                    recyclerView.adapter = adapter
                }
                //val adapter = ArrayAdapterCustom(activity, R.layout.my_list, events)
                //listView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
