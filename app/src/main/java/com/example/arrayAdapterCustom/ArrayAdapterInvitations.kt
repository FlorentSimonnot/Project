package com.example.arrayAdapterCustom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.events.Event
import com.example.project.R
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey

class ArrayAdapterInvitations(
    val ctx : Context,
    val resource : Int,
    val users : ArrayList<UserWithKey>,
    val keyEvent : String?,
    val action : String
    ) : ArrayAdapter<UserWithKey>(ctx, resource, users) {

    private val session = SessionUser(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)


        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.photo_user)
        val textView : TextView = view.findViewById(R.id.identity)
        val button : Button
        when(action){
            "cancel" -> button = view.findViewById(R.id.more)
            else -> throw Exception("Impossible action")
        }


        //imageView.setImageDrawable(ctx.resources.getDrawable(users[position].getLogo()))
        textView.text = users[position].user.firstName + " "+ users[position].user.name
        User().showPhotoUser(ctx, imageView, users[position].key)
        button.setOnClickListener {
            when(action){
                "confirm" -> {
                    Event().confirmParticipation(ctx, keyEvent, users[position].key!!)
                    users.removeAt(position)
                }
                "cancel" -> {

                }
                else -> throw Exception("Impossible action")
            }

        }

        return view
    }
}