package com.example.arrayAdapterCustom

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import com.example.dialog.BottomSheetDialogFriend
import com.example.events.Event
import com.example.place.SessionGooglePlace
import com.example.project.*
import com.example.session.SessionUser
import com.example.user.PrivacyAccount
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
    private val action : String,
    private val fragment : FragmentManager
): ArrayAdapter<UserWithKey>(ctx, resource, users){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.photo_user)
        val textView : TextView = view.findViewById(R.id.identity)

        if(users.size > 0) {
            textView.text = users[position].user.firstName + " " + users[position].user.name
            User().showPhotoUser(ctx, imageView, users[position].key)


            view.setOnClickListener {
                when (action) {
                    "friend" -> {
                        val dialog = BottomSheetDialogFriend(ctx, R.layout.bottom_sheet_layout_friend, users[position].key!!)
                        dialog.show(fragment, "USER OPTIONS")
                    }
                    "waiting" -> {
                        val userKey = users[position].key
                        val intent = Intent(ctx, RequestFriendActivity::class.java).putExtra("key", userKey)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        ctx.startActivity(intent)
                    }
                }
            }

        }

        return view
    }

}