package com.example.arrayAdapterCustom

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.events.Event
import com.example.place.SessionGooglePlace
import com.example.project.*
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import java.util.*
import kotlin.collections.ArrayList


class ArrayAdapterFriends(
    private val ctx : Context,
    private val resource : Int,
    private val users : ArrayList<UserWithKey>,
    private val action : String
): ArrayAdapter<UserWithKey>( ctx , resource, users){
    private var buttonRefuse : Button? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.photo_user)
        val textView : TextView = view.findViewById(R.id.identity)
        val button : Button
        var buttonSendMessage : Button? = null

        button = when(action){
            "waiting" -> {
                view.findViewById(R.id.btn_accept)
            }
            "friend" -> {
                view.findViewById(R.id.btn_remove)
            }
            else -> {
                throw Exception("Nope")
            }
        }
        if(action == "waiting"){
            buttonRefuse = view.findViewById(R.id.btn_refuse)
        }
        if(action == "friend"){
            buttonSendMessage = view.findViewById(R.id.send_message)
        }

        if(users.size > 0) {
            textView.text = users[position].user.firstName + " " + users[position].user.name
            User().showPhotoUser(ctx, imageView, users[position].key)

            button.setOnClickListener {
                when (action) {
                    "waiting" -> {
                        val item = getItem(position)
                        if(item != null){
                            val userKey = users[position].key
                            users.removeAt(position)
                            SessionUser().acceptFriend(ctx, userKey)
                        }
                    }
                    "friend" -> {
                        val item = getItem(position)
                        if(item != null){
                            val userKey = users[position].key

                            val deleteFriendAlert = AlertDialog.Builder(ctx)
                            deleteFriendAlert.setTitle("Are you sure?")
                            deleteFriendAlert.setMessage("This user will not be your friend anymore :(.\nConfirm?")
                            deleteFriendAlert.setPositiveButton("Yes"){_, _ ->
                                SessionUser().deleteFriend(ctx, userKey)
                                users.removeAt(position)
                            }
                            deleteFriendAlert.setNegativeButton("No"){_, _ ->

                            }
                            deleteFriendAlert.show()
                        }
                    }
                }
            }
            buttonRefuse?.setOnClickListener {
                val item = getItem(position)
                if(item != null){
                    val userKey = users[position].key
                    users.removeAt(position)
                    SessionUser().refuseFriend(ctx, userKey)
                }
            }
            buttonSendMessage?.setOnClickListener {
                val intent = Intent(ctx, Dialog::class.java)
                intent.putExtra("keyUser", users[position].key)
                ctx.startActivity(intent)
            }
        }

        return view
    }

}