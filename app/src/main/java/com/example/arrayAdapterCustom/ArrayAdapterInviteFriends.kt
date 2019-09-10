package com.example.arrayAdapterCustom

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
import com.example.user.UserWithKeyAndStatus

class ArrayAdapterInviteFriends(
    val ctx : Context,
    val resource : Int,
    val users : ArrayList<UserWithKeyAndStatus>,
    val keyEvent : String?
) : ArrayAdapter<UserWithKeyAndStatus>(ctx, resource, users) {

    private val session = SessionUser(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)


        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.photo_user)
        val textView : TextView = view.findViewById(R.id.identity)
        val button : Button = view.findViewById(R.id.send_invitation)

        textView.text = users[position].user.firstName + " "+ users[position].user.name
        User().showPhotoUser(ctx, imageView, users[position].key)

        if(users[position].status == "already"){
            button.text = ctx.getString(R.string.event_info_cancel_invitation)
            button.background = context.resources.getDrawable(R.drawable.rounded_button_rectangle_red)
            button.setOnClickListener {
                Event().deleteInvitationEvent(ctx, keyEvent, users[position].key!!)
                users.removeAt(position)
            }
        }
        else{
            button.text = ctx.getString(R.string.event_info_send_invitation)
            button.background = context.resources.getDrawable(R.drawable.rounded_button_rectangle_blue)
            button.setOnClickListener {
                Event().inviteFriend(ctx, keyEvent, users[position].key!!)
                users.removeAt(position)
            }
        }

        return view
    }
}