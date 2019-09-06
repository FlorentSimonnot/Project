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
import kotlinx.android.synthetic.main.cardview_suggestion.view.*
import kotlinx.android.synthetic.main.search_list_item_user.view.*


class SearchAdapterSuggestion(
    val context: Context,
    val resource : Int,
    val users : ArrayList<UserWithKey>,
    val onItemListener: SearchAdapterSuggestion.OnItemListener
) : RecyclerView.Adapter<SearchAdapterSuggestion.ViewHolder>() {

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

    }

    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener  {
        var name : TextView
        var photo : ImageView
        var onItemListener : OnItemListener

        constructor(view: View, onItemListener: OnItemListener) : super(view) {
            name = view.name
            photo = view.image_view
            this.onItemListener = onItemListener

            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onItemListener.onClickCardView(adapterPosition)
        }


    }

    interface OnItemListener{
        fun onClickCardView(position : Int)
        //fun onLongClick(position: Int)
    }

}
