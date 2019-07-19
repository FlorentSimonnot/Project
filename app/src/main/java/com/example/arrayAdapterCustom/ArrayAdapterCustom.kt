package com.example.arrayAdapterCustom

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.graphics.ColorSpace
import android.view.View
import android.widget.*
import com.example.events.Event
import com.example.project.R


class ArrayAdapterCustom(private val ctx : Context , private val resource : Int, private val events : ArrayList<Event>)
    : ArrayAdapter<Event>( ctx , resource, events){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater :LayoutInflater = LayoutInflater.from(ctx)

        val view : View = layoutInflater.inflate(resource , null )
        val imageView :ImageView = view.findViewById(R.id.icon_sport)
        var textView : TextView = view.findViewById(R.id.event_name)
        var textView1 : TextView = view.findViewById(R.id.event_desc)


        imageView.setImageDrawable(ctx.resources.getDrawable(events[position].sport.getLogo()))
        textView.text = events[position].name
        textView1.text = events[position].description


        return view
    }

}