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
import com.example.user.User

class ArrayAdapterInvitations(
    val ctx : Context,
    val resource : Int,
    val users : ArrayList<User>
    ) : ArrayAdapter<User>(ctx, resource, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)


        val view : View = layoutInflater.inflate(resource , null )
        //val imageView : ImageView = view.findViewById(R.id.icon_sport)
        val textView : TextView = view.findViewById(R.id.identity)


        //imageView.setImageDrawable(ctx.resources.getDrawable(users[position].getLogo()))
        textView.text = users[position].name


        return view
    }
}