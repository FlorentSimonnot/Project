package com.example.project

import android.content.Context
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
import com.example.user.UserWithKey
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class SearchBarActivity : AppCompatActivity() {
    private lateinit var searchBar : EditText
    private lateinit var recyclerView : RecyclerView
    private var users : ArrayList<UserWithKey> = ArrayList()
    private lateinit var searchAdapter : SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_bar)

        searchBar = findViewById(R.id.search_user)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        searchBar.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    setAdapter(p0.toString(), this@SearchBarActivity)
                } else {
                    users.clear()
                    recyclerView.removeAllViews()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Nothing
            }

        })
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
                            users.add(UserWithKey(user, snapshot.key))
                            counter++
                        }
                    }
                    if (counter == 15)
                        break
                }

                if(users.size > 0){
                    findViewById<RelativeLayout>(R.id.noResults).visibility = View.GONE
                    searchAdapter = SearchAdapter(context, R.layout.search_list_item_user, users )
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
}
