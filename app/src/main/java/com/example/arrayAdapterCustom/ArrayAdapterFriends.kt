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
    private val fragmentManager : FragmentManager
): ArrayAdapter<UserWithKey>( ctx , resource, users){
    private var buttonRefuse : ImageButton? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.photo_user)
        val textView : TextView = view.findViewById(R.id.identity)
        val button : ImageButton

        button = view.findViewById(R.id.more)
        if(action == "friend"){
            button.setImageDrawable(context.resources.getDrawable(R.drawable.ic_more))
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
                            SessionUser(context).acceptFriend(ctx, userKey)
                        }
                    }
                    "friend" -> {
                        button.setOnClickListener {
                            val dialog = BottomSheetDialogFriend(ctx, R.layout.bottom_sheet_layout_friend, users[position].key!!)
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
                    SessionUser(context).refuseFriend(ctx, userKey)
                }
            }
            view.setOnClickListener {
                if(users[position].user.privacyAccount == PrivacyAccount.Public) {
                    val intent = Intent(ctx, PublicUserActivity::class.java)
                    intent.putExtra("user", users[position].key)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    ctx.startActivity(intent)
                }
                else{
                    val intent = Intent(ctx, PrivateUserActivity::class.java)
                    intent.putExtra("user", users[position].key)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    ctx.startActivity(intent)
                }
            }
        }

        return view
    }

}