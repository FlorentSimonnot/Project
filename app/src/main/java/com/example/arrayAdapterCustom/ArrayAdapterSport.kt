package com.example.arrayAdapterCustom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.events.Event
import com.example.project.R
import com.example.sport.Sport

class ArrayAdapterSport(private val ctx : Context, private val resource : Int, private val sports : ArrayList<Sport>)
    : ArrayAdapter<Sport>( ctx , resource, sports){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)


        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.icon_sport)
        val textView : TextView = view.findViewById(R.id.name_sport)


        imageView.setImageDrawable(ctx.resources.getDrawable(sports[position].getLogoSport()))
        textView.text = sports[position].getNameSport()


        return view
    }

}