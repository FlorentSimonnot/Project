package com.example.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.view.marginRight
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dateCustom.DateCustom
import com.example.dateCustom.TimeCustom
import com.example.messages.*
import com.example.session.SessionUser
import com.example.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class Dialog : AppCompatActivity(), View.OnClickListener {
    private lateinit var keyUser : String
    private lateinit var keyChat : String
    private lateinit var buttonSendMessage : ImageButton
    private lateinit var buttonSendImage : ImageButton
    private lateinit var messageEditText: EditText
    private val session = SessionUser(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : MessageAdapter
    private val photoRequestCode = 1818
    private lateinit var uri : Uri
    private lateinit var llm : LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageViewOptions = findViewById<ImageView>(R.id.dots_menu)
        imageViewOptions.setOnClickListener(this)

        val infos : Bundle? = intent.extras
        keyUser = infos?.getString("keyUser").toString()
        keyChat = infos?.getString("keyChat").toString()

        if(keyChat.isEmpty()){
            throw Exception("KEY CHAT MUST NOT BE EMPTY")
        }
        if(keyUser.isEmpty()){
            throw Exception("KEY USER MUST NOT BE EMPTY")
        }

        buttonSendMessage = findViewById(R.id.send_message)
        buttonSendImage = findViewById(R.id.button_insert_image)
        messageEditText = findViewById(R.id.message)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)
        llm = LinearLayoutManager(this)
        llm.stackFromEnd = true

        buttonSendMessage.setOnClickListener(this)
        buttonSendImage.setOnClickListener(this)

        SessionUser(this).writeNameUserDiscussion(this, keyUser, supportActionBar!!)

        FirebaseDatabase.getInstance().getReference("discussions/$keyChat").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                searchMessages(this@Dialog)
            }

        })

        messageEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                //No
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               //No
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString().isNotEmpty()){
                    val lp = RelativeLayout.LayoutParams(messageEditText.layoutParams.width, messageEditText.layoutParams.height)
                    lp.setMargins(120, 0, 120, 0)
                    messageEditText.layoutParams = lp
                    buttonSendMessage.visibility = View.VISIBLE
                }
                else{
                    val lp = RelativeLayout.LayoutParams(messageEditText.layoutParams.width, messageEditText.layoutParams.height)
                    lp.setMargins(120, 0, 0, 0)
                    messageEditText.layoutParams = lp
                    buttonSendMessage.visibility = View.GONE
                }
            }

        })

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.send_message -> {
                val text = messageEditText.text.toString()
                if(text.isNotEmpty()){
                    val message = Message(
                        Utils().generatePassword(100),
                        Actor(session.getIdFromUser(), true),
                        Actor(keyUser, true),
                        text,
                        Date().time
                    )
                    message.insertMessage(this, messageEditText)
                }
            }
            R.id.button_insert_image -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, photoRequestCode)
            }
            R.id.dots_menu -> {
                val popupMenu = PopupMenu(this@Dialog, p0)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.action_delete -> {
                            val d = Discussion(keyChat)
                            d.deleteDiscussionForCurrentUser(this@Dialog)
                            return@setOnMenuItemClickListener true
                        }
                        else -> {
                            //Nothing
                            return@setOnMenuItemClickListener false
                        }
                    }
                }
                popupMenu.inflate(R.menu.menu_discussion)
                popupMenu.show()

            }
        }
    }

    private fun searchMessages(context: Context){
        val refKeyChat = FirebaseDatabase.getInstance().getReference("friends/${session.getIdFromUser()}/$keyUser")
        refKeyChat.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("keyChat")) {
                    val value = p0.child("keyChat").value as String
                    if (value != null) {
                        searchDiscussion(context, value)
                    }
                }
            }

        })
    }

    private fun searchDiscussion(context: Context, keyChat : String){
        val ref = FirebaseDatabase.getInstance().getReference("discussions/$keyChat/messages")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each messages
                val messages : ArrayList<Message> = ArrayList()
                data.forEach {
                    val message : Message = it.getValue(Message::class.java) as Message
                    if(message.sender.key == session.getIdFromUser()){
                        if(message.sender.visible){
                            messages.add(message)
                        }
                    }
                    else{
                        if(message.addressee.visible){
                            messages.add(message)
                        }
                    }
                }
                if(messages.size > 0){
                    val sortedList = messages.sortedWith(compareBy({it.dateTime})).toList()
                    val lastMessage = sortedList[sortedList.size - 1]
                    if(lastMessage.addressee.key == session.getIdFromUser()) {
                        lastMessage.seeMessage(keyChat)
                    }
                    adapter = MessageAdapter(context,
                        R.layout.list_item_message_me,
                        R.layout.list_item_message,
                        R.layout.list_item_message_image_me,
                        R.layout.list_item_message_image,
                        ArrayList(sortedList),
                        recyclerView
                    )
                    adapter.notifyDataSetChanged()
                    adapter.setSelected(messages.size)

                    if(adapter.itemCount > 0)
                        llm.scrollToPosition(adapter!!.itemCount - 1)

                    recyclerView.layoutManager = llm
                    recyclerView.adapter = adapter

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data != null) {
            when(requestCode){
                photoRequestCode -> {
                    if(resultCode == Activity.RESULT_OK){
                        uri = data.data!!
                        val urlPhoto = UUID.randomUUID().toString()
                        Utils().insertImage(urlPhoto, uri, session.getIdFromUser(), keyUser)
                        val message = Message(
                            Utils().generatePassword(100),
                            Actor(session.getIdFromUser(), true),
                            Actor(keyUser, true),
                            "",
                            Date().time,
                            TypeMessage.IMAGE,
                            urlPhoto
                        )
                        message.insertMessage(this, messageEditText)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
