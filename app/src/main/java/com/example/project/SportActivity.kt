package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.ListView
import com.example.arrayAdapterCustom.ArrayAdapterSport
import com.example.sport.Sport
import android.widget.TextView



class SportActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var gridView: GridView
    private lateinit var sportList : ArrayList<Sport>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sport)

        gridView = findViewById(R.id.grid)
        sportList = ArrayList()

        createSportList()

        gridView.adapter = ArrayAdapterSport(this, R.layout.gridview_item_sport, sportList)

    }

    private fun createSportList(){
        Sport.values().forEach{
            if(it != Sport.INIT){
                sportList.add(it)
            }
        }
    }

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
