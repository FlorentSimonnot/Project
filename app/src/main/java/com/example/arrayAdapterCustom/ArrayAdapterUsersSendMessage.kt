package com.example.arrayAdapterCustom

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.events.Event
import com.example.project.Dialog
import com.example.project.R
import com.example.user.User
import com.example.user.UserWithKey

class ArrayAdapterUsersSendMessage(
    private val ctx : Context,
    private val resource : Int,
    private val users : ArrayList<UserWithKey>
): ArrayAdapter<UserWithKey>( ctx , resource, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.photo_user)
        val textView : TextView = view.findViewById(R.id.identity)

        if(users.size > 0) {
            textView.text = users[position].user.firstName + " " + users[position].user.name
            User().showPhotoUser(ctx, imageView, users[position].key)
            view.setOnClickListener{
                val intent = Intent(ctx, Dialog::class.java)
                intent.putExtra("keyUser", users[position].key)
                ctx.startActivity(intent)
            }
        }

        return view
    }
}