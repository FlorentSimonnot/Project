package com.example.arrayAdapterCustom

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.example.dialog.BottomSheetDialogFriend
import com.example.dialog.BottomSheetDialogParticipantsEvent
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


class ArrayAdapterCustomUsers(
    private val ctx : Context,
    private val resource : Int,
    private val keyEvent : String?,
    private val users : ArrayList<UserWithKey>,
    private val action : String,
    private val fragmentManager: FragmentManager
): ArrayAdapter<UserWithKey>( ctx , resource, users){
    private var buttonRefuse : ImageButton? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.photo_user)
        val textView : TextView = view.findViewById(R.id.identity)
        val button : ImageButton

        button = when(action){
            "waiting" -> {
                view.findViewById(R.id.btn_accept)
            }
            "confirm" -> {
                view.findViewById(R.id.more)
            }
            else -> {
                throw Exception("Nope")
            }
        }
        if(action == "waiting"){
            buttonRefuse = view.findViewById(R.id.btn_refuse)
        }

        if(users.size > 0) {
            Event().hideMore(ctx, keyEvent, textView, button, users[position].key)
            textView.text = users[position].user.firstName + " " + users[position].user.name
            User().showPhotoUser(ctx, imageView, users[position].key)

            button.setOnClickListener {
                when (action) {
                    "waiting" -> {
                        val item = getItem(position)
                        if(item != null){
                            val userKey = users[position].key
                            users.removeAt(position)
                            Event().confirmParticipation(ctx, keyEvent, userKey!!)
                        }
                    }
                    "confirm" -> {
                        button.setOnClickListener {
                            val dialog = BottomSheetDialogParticipantsEvent(ctx, R.layout.bottom_sheet_layout_friend, keyEvent!!, users[position].key!!)
                            dialog.show(fragmentManager, "USER OPTIONS")
                        }
                    }
                }
            }
            buttonRefuse?.setOnClickListener {
                val item = getItem(position)
                if(item != null){
                    val userKey = users[position].key
                    users.removeAt(position)
                    Event().refuseParticipation(ctx, keyEvent, userKey!!)
                }
            }
        }

        return view
    }

}