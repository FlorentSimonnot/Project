package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.core.view.size
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
import kotlinx.android.synthetic.main.activity_messagerie.*

class MessagerieActivity : AppCompatActivity(), View.OnClickListener, LatestMessageAdapter.OnItemListener {

    private lateinit var newMessage : ImageButton
    private val session = SessionUser(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar : RelativeLayout
    private lateinit var searchEditText : EditText
    private lateinit var notFound : RelativeLayout
    private lateinit var adapter : LatestMessageAdapter

    val keysChat = ArrayList<String>()
    val friends = ArrayList<String>()
    var latestDiscussion = ArrayList<DiscussionViewLastMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagerie)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val menu = MenuCustom(this, navView, this@MessagerieActivity)
        navView.setOnNavigationItemSelectedListener(menu.onNavigationItemSelectedListener)

        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.layout_search)
        notFound = findViewById(R.id.noResults)
        searchEditText = findViewById(R.id.searchUser)

        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        recyclerView.layoutManager = llm


        newMessage = findViewById(R.id.btn_new_message)
        newMessage.setOnClickListener(this)

        searchEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                friendsList(this@MessagerieActivity, p0?.toString()!!)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        //Actualise badges when changes and messages
        FirebaseDatabase.getInstance().getReference("discussions").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                menu.setBadges()
                friendsList(this@MessagerieActivity, "")
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


    /**
     * friendsList(context : Context, stringSearch : String)
     * collects all friends of current user
     * @param context : Context
     * @param stringSearch : String if we want select users in accordance to characters
     */
    private fun friendsList(context: Context, stringSearch : String){
        friends.clear()
        keysChat.clear()
        latestDiscussion.clear()

        val ref = FirebaseDatabase.getInstance().getReference("friends/${this.session.getIdFromUser()}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each friends
                data.forEach {
                    friends.add(it.key!!)
                }
                searchUser(context, friends, stringSearch)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     * fun searchUser(context : Context, friends : ArrayList<String>, stringSearch: String)
     * collect friends of current user if their name contains the stringSearch
     * @param context : Context
     * @param friends : ArrayList<String> all current user's friends
     * @param stringSearch : String if we want select users in accordance to characters
     *
     */
    private fun searchUser(context : Context, friends : ArrayList<String>, stringSearch: String){
        val users = ArrayList<String>()
        friends.forEachIndexed{ index, it ->
            FirebaseDatabase.getInstance().getReference("users/$it").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val firstName = p0.child("firstName").value as String
                    val name = p0.child("name").value as String
                    if(stringSearch.isNotEmpty()){
                        if(firstName.contains(stringSearch) || name.contains(stringSearch)){
                            users.add(p0.key!!)
                            println("STRING $stringSearch")
                        }
                        else{
                            println("NO STRING $stringSearch")
                        }
                    }
                    else{
                        users.add(p0.key!!)
                    }

                    if(index == friends.size - 1){
                        searchDiscussion(context, users)
                    }

                }

            })
        }
    }

    /**
     * searchDiscussion(context: Context, users : ArrayList<String>)
     * collect the chat key for every current user's friend
     * @param context : Context
     * @param users : ArrayList<String> all current user's friend
     *
     */
    private fun searchDiscussion(context: Context, users : ArrayList<String>){
        keysChat.clear()
        if(users.size == 0){
            searchLatestMessages(context, keysChat)
        }else {
            users.forEachIndexed { index, it ->
                FirebaseDatabase.getInstance().getReference("friends/${session.getIdFromUser()}/$it")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChild("keyChat")) {
                                val keyChat = p0.child("keyChat").value as String
                                keysChat.add(keyChat)
                            }

                            if (index == users.size - 1) {
                                searchLatestMessages(context, keysChat)
                            }

                        }

                    })
            }
        }
    }

    private fun searchLatestMessages(context: Context, keys : ArrayList<String>){
        if(keys.size == 0){
            recyclerView.visibility = View.GONE
            noResults.visibility = View.VISIBLE
        }
        else {
            keys.forEachIndexed { index, it ->
                val ref = FirebaseDatabase.getInstance().getReference("discussions/$it/messages")
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val data = dataSnapshot.children //Children = each messages
                        val messages: ArrayList<Message> = ArrayList()

                        //Collect all messages
                        data.forEach {
                            val message: Message = it.getValue(Message::class.java) as Message
                            messages.add(message)
                        }
                        val sortedList = ArrayList(messages.sortedWith(compareBy({ it.date }, { it.time })).toList())
                        if (sortedList.size > 0) {
                            latestDiscussion.add(
                                DiscussionViewLastMessage(
                                    it,
                                    sortedList[sortedList.size - 1]
                                )
                            )
                        }
                        if (index == keys.size - 1) {
                            if (latestDiscussion.size > 0) {
                                recyclerView.visibility = View.VISIBLE
                                noResults.visibility = View.GONE
                                latestDiscussion = ArrayList(
                                    latestDiscussion.sortedWith(
                                        compareBy({ it.lastMessage.date },
                                            { it.lastMessage.time })
                                    ).toList()
                                )
                                latestDiscussion.reverse()
                                adapter = LatestMessageAdapter(
                                    context,
                                    R.layout.list_item_last_message,
                                    latestDiscussion,
                                    this@MessagerieActivity
                                )
                                recyclerView.adapter = adapter
                                if (adapter.itemCount > 0) {
                                    val position = recyclerView.adapter!!.itemCount
                                    recyclerView.smoothScrollToPosition(position - 1)
                                }
                            } else {
                                recyclerView.visibility = View.GONE
                                noResults.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
    }

    override fun onClick(position: Int) {
        if(position < keysChat.size) {
            val intent = Intent(this@MessagerieActivity, Dialog::class.java)
            if(latestDiscussion[position].lastMessage.sender.key == session.getIdFromUser()){
                intent.putExtra("keyUser", latestDiscussion[position].lastMessage.addressee.key)
            }else {
                intent.putExtra("keyUser", latestDiscussion[position].lastMessage.sender.key)
            }
            intent.putExtra("keyChat", latestDiscussion[position].keyChat)
            this@MessagerieActivity.startActivity(intent)
        }

    }


}
