package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.widget.Toolbar
import com.example.color.Color
import com.example.color.ColorWithHexa
import com.example.color.ColorsAdapter

class ChooseBackgroundColor : AppCompatActivity() {
    private lateinit var keyChat : String
    private lateinit var gridView : GridView
    private var colors = ArrayList<ColorWithHexa>()
    private var hexaValues = intArrayOf(100, 200, 300, 400, 500, 600, 700, 800, 900)
    private lateinit var adapterGridView : ColorsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_background_color)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Change color background"

        val infos : Bundle? = intent.extras
        keyChat = infos?.getString("keyChat").toString()
        gridView = findViewById(R.id.gridView)

        setColors()
        adapterGridView = ColorsAdapter(this, R.layout.color_item, colors, keyChat)
        gridView.adapter = adapterGridView

    }


    private fun setColors(){
        Color.values().forEach{
            if(it != Color.WHITE) {
                val color = it
                hexaValues.forEach {
                    colors.add(ColorWithHexa(color, it))
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
