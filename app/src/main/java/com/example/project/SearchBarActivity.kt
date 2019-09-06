package com.example.project

import android.content.Context
import android.content.Intent
import android.content.SearchRecentSuggestionsProvider
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.user.User
import com.google.firebase.database.DatabaseError
import com.example.search.SearchAdapter
import com.google.firebase.database.DataSnapshot
import android.text.method.TextKeyListener.clear
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.search.SearchAdapterSuggestion
import com.example.session.SessionUser
import com.example.user.PrivacyAccount
import com.example.user.UserWithKey
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class SearchBarActivity : AppCompatActivity(), SearchAdapter.OnItemListener, SearchAdapterSuggestion.OnItemListener {

    private lateinit var searchBar : EditText
    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerViewSuggestion : RecyclerView
    private var users : ArrayList<UserWithKey> = ArrayList()
    private var usersSuggestion : ArrayList<UserWithKey> = ArrayList()
    private lateinit var searchAdapter : SearchAdapter
    private lateinit var searchAdapterSuggestions : SearchAdapterSuggestion
    private lateinit var suggestions : RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_bar)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        searchBar = findViewById(R.id.search_user)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewSuggestion = findViewById(R.id.recyclerViewSuggestion)
        suggestions = findViewById(R.id.suggestions)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        recyclerViewSuggestion.setHasFixedSize(true)
        recyclerViewSuggestion.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        searchBar.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    suggestions.visibility = View.GONE
                    setAdapter(p0.toString(), this@SearchBarActivity)
                } else {
                    users.clear()
                    recyclerView.removeAllViews()
                    suggestions.visibility = View.VISIBLE
                    findViewById<RelativeLayout>(R.id.noResults).visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Nothing
            }

        })

        setAdapterUsersSuggestion(this)
    }

    private fun setAdapter(searchedString: String, context: Context) {
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                recyclerView.removeAllViews()

                var counter = 0

                /*
                * Search all users for matching searched string
                * */
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if(user != null){
                        if(
                            user.name.toUpperCase().contains(searchedString.toUpperCase()) ||
                            user.firstName.toUpperCase().contains(searchedString.toUpperCase())
                        ){
                            if (snapshot.key != SessionUser(context).getIdFromUser()){
                                users.add(UserWithKey(user, snapshot.key))
                            counter++
                            }
                        }
                    }
                    if (counter == 15)
                        break
                }

                if(users.size > 0){
                    findViewById<RelativeLayout>(R.id.noResults).visibility = View.GONE
                    suggestions.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    searchAdapter = SearchAdapter(context, R.layout.search_list_item_user, users ,this@SearchBarActivity)
                    recyclerView.adapter = searchAdapter
                }
                else{
                    findViewById<RelativeLayout>(R.id.noResults).visibility = View.VISIBLE
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setAdapterUsersSuggestion(context: Context){
        var eventsJoined = ArrayList<String>()
        FirebaseDatabase.getInstance().getReference("users/${SessionUser(context).getIdFromUser()}/eventsJoined").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    eventsJoined.add(it.key!!)
                }

                setAdapterUsersSuggestionAux(context, eventsJoined)
            }


        })
    }

    private fun setAdapterUsersSuggestionAux(context: Context, eventsJoined : ArrayList<String>) {
        var users = ArrayList<String>()
        eventsJoined.forEach {
            FirebaseDatabase.getInstance().getReference("events/$it/participants").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        if(it.child("status").value == "confirmed" && it.key != SessionUser(context).getIdFromUser()){
                            users.add(it.key!!)
                        }
                    }

                    setAdapterUsersSuggestionAuxAux(context, users)
                }
            })
        }
    }

    private fun setAdapterUsersSuggestionAuxAux(context: Context, users : ArrayList<String>){
        var counter = 0
        users.forEachIndexed { index, it ->
            FirebaseDatabase.getInstance().getReference("users/$it").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    usersSuggestion.clear()
                    recyclerViewSuggestion.removeAllViews()

                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        if (dataSnapshot.key != SessionUser(context).getIdFromUser()) {
                            usersSuggestion.add(UserWithKey(user, dataSnapshot.key))
                            counter++
                        }
                    }

                    if(index == users.size-1 || counter == 15){
                        if (usersSuggestion.size > 0) {
                            findViewById<RelativeLayout>(R.id.noResults).visibility = View.GONE
                            searchAdapterSuggestions = SearchAdapterSuggestion(
                                context,
                                R.layout.cardview_suggestion,
                                usersSuggestion,
                                this@SearchBarActivity
                            )
                            recyclerViewSuggestion.adapter = searchAdapterSuggestions
                        } else {
                            suggestions.visibility = View.GONE
                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(position: Int) {
        when(users[position].user.privacyAccount){
            PrivacyAccount.Public -> {
                val intent = Intent(this, PublicUserActivity::class.java)
                intent.putExtra("user", users[position].key)
                startActivity(intent)
            }
            PrivacyAccount.Private -> {
                val intent = Intent(this, PrivateUserActivity::class.java)
                intent.putExtra("user", users[position].key)
                startActivity(intent)
            }
        }
    }

    override fun onClickCardView(position: Int) {
        when(usersSuggestion[position].user.privacyAccount){
            PrivacyAccount.Public -> {
                val intent = Intent(this, PublicUserActivity::class.java)
                intent.putExtra("user", usersSuggestion[position].key)
                startActivity(intent)
            }
            PrivacyAccount.Private -> {
                val intent = Intent(this, PrivateUserActivity::class.java)
                intent.putExtra("user", usersSuggestion[position].key)
                startActivity(intent)
            }
        }
    }

    override fun onLongClick(position: Int) {}
}
