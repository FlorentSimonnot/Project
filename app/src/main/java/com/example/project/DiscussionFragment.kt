package com.example.project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.discussion.LatestMessageAdapter
import com.example.messages.DiscussionViewLastMessage
import com.example.messages.Message
import com.example.session.SessionUser
import com.example.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_messagerie.*


class DiscussionFragment : Fragment(), LatestMessageAdapter.OnItemListener {
    private lateinit var session : SessionUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar : RelativeLayout
    private lateinit var searchEditText : EditText
    private lateinit var notFound : RelativeLayout
    private lateinit var adapter : LatestMessageAdapter

    var keysChat = ArrayList<String>()
    var friends = ArrayList<String>()
    var latestDiscussion = ArrayList<DiscussionViewLastMessage>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_discussion, container, false)

        session = SessionUser(context!!)
        recyclerView = view.findViewById(R.id.recyclerView)
        searchBar = view.findViewById(R.id.layout_search)
        notFound = view.findViewById(R.id.noResults)
        searchEditText = view.findViewById(R.id.searchUser)

        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(context!!)
        recyclerView.layoutManager = llm


        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                friendsList(context!!, p0?.toString()!!)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        var ctx = context!!

        //Actualise badges when changes and messages
        FirebaseDatabase.getInstance().getReference("discussions").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                friendsList(ctx, "")
            }

        })
        return view
    }

    /*--------CUSTOM MENU FOR THIS FRAGMENT-------------------*/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.discussions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_new_message -> {
                context!!.startActivity(Intent(context!!, ListUserSendMessage::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
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

        println("latestDiscussion $latestDiscussion")

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
            FirebaseDatabase.getInstance().getReference("users/$it").addValueEventListener(object : ValueEventListener {
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
            searchLatestMessages(context)
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
                                searchLatestMessages(context)
                            }

                        }

                    })
            }
        }
    }

    private fun searchLatestMessages(context: Context){
        latestDiscussion.clear()
        keysChat = Utils().removeDuplicates(keysChat)
        if(keysChat.size == 0){
            recyclerView.visibility = View.GONE
            notFound.visibility = View.VISIBLE
        }
        else {
            keysChat.forEachIndexed { index, it ->
                val ref = FirebaseDatabase.getInstance().getReference("discussions/$it/messages")
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val data = dataSnapshot.children //Children = each messages
                        val messages: ArrayList<Message> = ArrayList()

                        //Collect all messages
                        data.forEach {
                            val message: Message = it.getValue(Message::class.java) as Message
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
                        val sortedList = ArrayList(messages.sortedWith(compareBy({ it.dateTime})).toList())
                        if (sortedList.size > 0) {
                            latestDiscussion.add(
                                DiscussionViewLastMessage(
                                    it,
                                    sortedList[sortedList.size - 1]
                                )
                            )
                        }
                        if (index == keysChat.size - 1) {
                            if (latestDiscussion.size > 0) {
                                recyclerView.visibility = View.VISIBLE
                                notFound.visibility = View.GONE
                                latestDiscussion = ArrayList(
                                    latestDiscussion.sortedWith(
                                        compareBy({ it.lastMessage.dateTime})
                                    ).toList()
                                )
                                latestDiscussion.reverse()
                                adapter = LatestMessageAdapter(
                                    context,
                                    R.layout.list_item_last_message,
                                    latestDiscussion,
                                    this@DiscussionFragment
                                )
                                recyclerView.adapter = adapter
                                if (adapter.itemCount > 0) {
                                    val position = recyclerView.adapter!!.itemCount
                                    recyclerView.smoothScrollToPosition(position - 1)
                                }
                            } else {
                                recyclerView.visibility = View.GONE
                                notFound.visibility = View.VISIBLE
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
            val intent = Intent(context, Dialog::class.java)
            if(latestDiscussion[position].lastMessage.sender.key == session.getIdFromUser()){
                intent.putExtra("keyUser", latestDiscussion[position].lastMessage.addressee.key)
            }else {
                intent.putExtra("keyUser", latestDiscussion[position].lastMessage.sender.key)
            }
            intent.putExtra("keyChat", latestDiscussion[position].keyChat)
            context!!.startActivity(intent)
        }

    }


}
