package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.GridView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import com.example.arrayAdapterCustom.ArrayAdapterSport
import com.example.arrayAdapterCustom.ArrayAdapterSportWithBoolean
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.sport.SportWithBoolean
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsSport : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var sportList : ArrayList<SportWithBoolean>
    private var sportListClone = ArrayList<SportWithBoolean>()
    private lateinit var adapter : ArrayAdapterSportWithBoolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_sport)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Your preferences"

        listView = findViewById(R.id.listView)
        sportList = ArrayList()

        createSportList("")
    }

    private fun createSportList(filter : String?){
        FirebaseDatabase.getInstance().getReference("sports/${SessionUser(this@SettingsSport).getIdFromUser()}/parameters").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                sportList.clear()
                val sports = p0.children
                sports.forEach {
                    sportList.add(SportWithBoolean(Sport.valueOf(it.key!!), it.value as Boolean))
                }
                var position = 0
                if(sportListClone.size == 0){
                    sportListClone = sportList
                }
                else{
                    if(sportListClone.size == sportList.size){
                        for(i in 0 until sportList.size){
                            if(sportList[i].boolean != sportListClone[i].boolean){
                                position = i
                            }
                        }
                        sportListClone = sportList
                    }
                }
                sportList = ArrayList(sportList.sortedWith(compareBy({it.sport.name})))
                adapter = ArrayAdapterSportWithBoolean(this@SettingsSport, R.layout.listview_item_sport, sportList)
                adapter.setNotifyOnChange(false)
                listView.adapter = adapter
                listView.setSelection(position)
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
