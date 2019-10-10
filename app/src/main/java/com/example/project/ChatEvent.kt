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
import com.example.events.Event
import com.example.messages.*
import com.example.messages.ChatEvent
import com.example.session.SessionUser
import com.example.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat_event.*
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class ChatEvent : AppCompatActivity(), View.OnClickListener {
    private lateinit var keyChat : String
    private lateinit var buttonSendMessage : ImageButton
    private lateinit var buttonSendImage : ImageButton
    private lateinit var messageEditText: EditText
    private lateinit var discussionTurnOff : TextView
    private val session = SessionUser(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : MessageAdapterChatEvent
    private val photoRequestCode = 1818
    private lateinit var uri : Uri
    private lateinit var llm : LinearLayoutManager
    private lateinit var bodyLayout : RelativeLayout
    private lateinit var footer : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_event)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageViewOptions = findViewById<ImageView>(R.id.dots_menu)
        imageViewOptions.setOnClickListener(this)

        val infos : Bundle? = intent.extras
        keyChat = infos?.getString("keyChat").toString()


        buttonSendMessage = findViewById(R.id.send_message)
        buttonSendImage = findViewById(R.id.button_insert_image)
        messageEditText = findViewById(R.id.message)
        recyclerView = findViewById(R.id.recyclerView)
        discussionTurnOff = findViewById(R.id.discussion_turn_off)
        bodyLayout = findViewById(R.id.body)
        footer = findViewById(R.id.footer)

        recyclerView.setHasFixedSize(true)
        llm = LinearLayoutManager(this)
        llm.stackFromEnd = true

        buttonSendMessage.setOnClickListener(this)
        buttonSendImage.setOnClickListener(this)

        FirebaseDatabase.getInstance().getReference("chatEvent/$keyChat/parameters").addValueEventListener(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    ChatEvent(keyChat).isDiscussionClosed(messageEditText, buttonSendImage, buttonSendMessage, discussionTurnOff)
                    ChatEvent(keyChat).setBackgroundColor(bodyLayout, supportActionBar!!, footer)
                    ChatEvent(keyChat).setTitleDiscussion(this@ChatEvent, supportActionBar!!)
                }

            }
        )

        searchDiscussion(this, keyChat)

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
                        Actor("", true),
                        text,
                        Date().time
                    )
                    message.insertMessageChatEvent(this, messageEditText, keyChat)
                }
            }
            R.id.button_insert_image -> {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, photoRequestCode)
            }
            R.id.dots_menu -> {
                val popupMenu = PopupMenu(this@ChatEvent, p0)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.action_info_group -> {
                            startActivity(Intent(this@ChatEvent, GroupInformationEventDiscussion::class.java).putExtra("keyChat", keyChat))
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_clear_discussion-> {
                            return@setOnMenuItemClickListener true
                        }
                        else -> {
                            //Nothing
                            return@setOnMenuItemClickListener false
                        }
                    }
                }
                popupMenu.inflate(R.menu.menu_chat_event)
                popupMenu.show()
            }
        }
    }


    private fun searchDiscussion(context: Context, keyChat : String){
        val ref = FirebaseDatabase.getInstance().getReference("chatEvent/$keyChat/messages")
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
                    adapter = MessageAdapterChatEvent(context,
                        R.layout.list_item_message_me,
                        R.layout.list_item_message_with_identity,
                        R.layout.list_item_message_image_me,
                        R.layout.list_item_message_image_with_identity,
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
                        //Utils().insertImage(urlPhoto, uri, session.getIdFromUser(), keyUser)
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
