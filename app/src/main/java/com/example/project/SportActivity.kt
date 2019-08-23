package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.example.arrayAdapterCustom.ArrayAdapterSport
import com.example.events.Event
import com.example.events.EventFirstStep
import com.example.sport.Sport
import kotlinx.android.synthetic.main.activity_create_event.*


class SportActivity : AppCompatActivity() {
    private lateinit var searchBar : EditText
    private lateinit var gridView: GridView
    private lateinit var sportList : ArrayList<Sport>
    private var eventFirstStep = EventFirstStep()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sport)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        eventFirstStep = intent.getSerializableExtra("event") as EventFirstStep

        searchBar = findViewById(R.id.search_user)
        gridView = findViewById(R.id.grid)
        sportList = ArrayList()

        createSportList("")

        gridView.adapter = ArrayAdapterSport(this, R.layout.gridview_item_sport, sportList)
        gridView.setOnItemClickListener { adapterView, view, i, l ->
            eventFirstStep.sport = sportList[i]
            val intent = Intent(this, CreateEventActivity::class.java)
            intent.putExtra("event", eventFirstStep)
            startActivity(intent)
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    createSportList(p0.toString())
                    gridView.adapter = ArrayAdapterSport(this@SportActivity, R.layout.gridview_item_sport, sportList)
                    gridView.setOnItemClickListener { adapterView, view, i, l ->
                        eventFirstStep.sport = sportList[i]
                        val intent = Intent(this@SportActivity, CreateEventActivity::class.java)
                        intent.putExtra("event", eventFirstStep)
                        startActivity(intent)
                    }
                } else {
                    createSportList("")
                    gridView.adapter = ArrayAdapterSport(this@SportActivity, R.layout.gridview_item_sport, sportList)
                    gridView.setOnItemClickListener { adapterView, view, i, l ->
                        eventFirstStep.sport = sportList[i]
                        val intent = Intent(this@SportActivity, CreateEventActivity::class.java)
                        intent.putExtra("event", eventFirstStep)
                        startActivity(intent)
                    }
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

    private fun createSportList(filter : String?){
        sportList.clear()
        Sport.values().forEach{
            if(it != Sport.INIT){
                if(filter!!.isNotEmpty()){
                    if(it.name.contains(filter!!.toUpperCase())){
                        sportList.add(it)
                    }
                }else {
                    sportList.add(it)
                }
            }
        }
        sportList = ArrayList(sportList.sortedWith(compareBy({it.name})))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val intent = Intent(this, CreateEventActivity::class.java)
        startActivity(intent)
        return true
    }

}
