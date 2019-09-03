package com.example.search

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.user.User
import android.R
import android.annotation.SuppressLint
import android.se.omapi.Session
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.discussion.NotificationsAdapter
import com.example.session.SessionUser
import com.example.user.UserWithKey
import kotlinx.android.synthetic.main.search_list_item_user.view.*


class SearchAdapter(
    val context: Context,
    val resource : Int,
    val users : ArrayList<UserWithKey>,
    val onItemListener: SearchAdapter.OnItemListener
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        return ViewHolder(view, onItemListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        User().showPhotoUser(context, holder.photo, users[position].key)
        holder.name.text = "${users.get(position).user.firstName} ${users.get(position).user.name}"
        holder.desc.text = users[position].user.description

    }

    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener  {
        var name : TextView
        var desc : TextView
        var photo : ImageView
        var onItemListener : OnItemListener

        constructor(view: View, onItemListener: OnItemListener) : super(view) {
            name = view.full_name
            desc = view.user_name
            photo = view.profile_photo
            this.onItemListener = onItemListener

            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onItemListener.onClick(adapterPosition)
        }


    }

    interface OnItemListener{
        fun onClick(position : Int)
        fun onLongClick(position: Int)
    }

}
