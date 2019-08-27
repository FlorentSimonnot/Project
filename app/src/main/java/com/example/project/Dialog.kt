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
import com.example.messages.Message
import com.example.messages.MessageAdapter
import com.example.messages.TypeMessage
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
    private val session = SessionUser()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : MessageAdapter
    private val photoRequestCode = 1818
    private lateinit var uri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val infos : Bundle? = intent.extras
        keyUser = infos?.getString("keyUser").toString()
        keyChat = infos?.getString("keyChat").toString()

        if(keyChat.isEmpty()){
            throw Exception("KEY CHAT MUST NOT BE EMPTY")
        }

        buttonSendMessage = findViewById(R.id.send_message)
        buttonSendImage = findViewById(R.id.button_insert_image)
        messageEditText = findViewById(R.id.message)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        recyclerView.layoutManager = llm

        buttonSendMessage.setOnClickListener(this)
        buttonSendImage.setOnClickListener(this)

        SessionUser().writeNameUserDiscussion(this, keyUser, supportActionBar!!)

        searchMessages(this)
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
                        session.getIdFromUser(),
                        keyUser,
                        text,
                        DateCustom("00/00/0000").getCurrentDate().toString(),
                        TimeCustom("00:00:00").getCurrentTime().toString()
                    )
                    message.insertMessage(this, messageEditText)
                }
            }
            R.id.button_insert_image -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, photoRequestCode)
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
                val value = p0.child("keyChat").value as String
                if(value != null){
                    searchDiscussion(context, value)
                }
            }

        })
    }

    private fun searchDiscussion(context: Context, keyChat : String){
        val ref = FirebaseDatabase.getInstance().getReference("discussions/$keyChat")
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
                if(messages.size > 0){
                    val sortedList = messages.sortedWith(compareBy({it.date}, {it.time})).toList()
                    val lastMessage = sortedList[sortedList.size - 1]
                    if(lastMessage.addressee == session.getIdFromUser())
                        lastMessage.seeMessage(keyChat)
                    adapter = MessageAdapter(context,
                        R.layout.list_item_message_me,
                        R.layout.list_item_message,
                        R.layout.list_item_message_image_me,
                        R.layout.list_item_message_image,
                        ArrayList(sortedList)
                    )
                    recyclerView.adapter = adapter
                    if(adapter.itemCount > 0) {
                        val position = recyclerView.adapter!!.itemCount
                        recyclerView.smoothScrollToPosition(position)
                    }
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
                            session.getIdFromUser(),
                            keyUser,
                            "",
                            DateCustom("00/00/0000").getCurrentDate().toString(),
                            TimeCustom("00:00:00").getCurrentTime().toString(),
                            false,
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
        val intent = Intent(this, MessagerieActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        Dialog::finish
        startActivity(intent)
        return true
    }
}
