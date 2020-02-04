package com.example.color

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import com.example.project.ChatEvent
import com.example.project.GroupInformationEventDiscussion
import com.example.project.R

class ColorsAdapter(
    val context: Context,
    val resource : Int,
    val colors : ArrayList<ColorWithHexa>,
    val keyChat : String
) : BaseAdapter() {
    private lateinit var buttonColor : Button

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )

        buttonColor = view.findViewById(R.id.buttonColor)
        val color = Color.parseColor(colors[p0].color.getHexa(colors[p0].hexa))
        buttonColor.setBackgroundColor(color)
        buttonColor.setOnClickListener {
            com.example.messages.ChatEvent(keyChat).changeBackgroundColor(colors[p0])
        }

        return view
    }

    override fun getItem(p0: Int): Any {
        return colors[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return colors.size
    }
}