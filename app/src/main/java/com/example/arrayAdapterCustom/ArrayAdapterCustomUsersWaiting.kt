package com.example.arrayAdapterCustom

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.example.dialog.BottomSheetDialogFriend
import com.example.dialog.BottomSheetDialogParticipantsEvent
import com.example.dialog.BottomSheetDialogParticipantsWaitingEvent
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


class ArrayAdapterCustomUsersWaiting(
    private val ctx : Context,
    private val resource : Int,
    val keyEvent: String,
    private val users : ArrayList<UserWithKey>,
    private val fragmentManager : FragmentManager
): ArrayAdapter<UserWithKey>( ctx , resource, users){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.photo_user)
        val textView : TextView = view.findViewById(R.id.identity)

        if(users.size > 0) {
            textView.text = "${users[position].user.firstName} ${users[position].user.name}"
            User().showPhotoUser(ctx, imageView, users[position].key)

            view.setOnClickListener {
                val dialog = BottomSheetDialogParticipantsWaitingEvent(ctx, R.layout.bottom_sheet_layout_participants_wait, keyEvent, users[position].key!!)
                dialog.show(fragmentManager, "USER OPTIONS")
            }

            /*button.setOnClickListener {
                when (action) {
                    "waiting" -> {
                        button.setOnClickListener {
                            val dialog = BottomSheetDialogParticipantsWaitingEvent(ctx, R.layout.bottom_sheet_layout_participants_wait, keyEvent!!, users[position].key!!)
                            dialog.show(fragmentManager, "USER OPTIONS")
                        }
                    }
                    "confirm" -> {
                        button.setOnClickListener {
                            val dialog = BottomSheetDialogParticipantsEvent(ctx, R.layout.bottom_sheet_layout_friend, keyEvent!!, users[position].key!!)
                            dialog.show(fragmentManager, "USER OPTIONS")
                        }
                    }
                }
            }*/
        }

        return view
    }

}