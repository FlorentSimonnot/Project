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
import com.example.session.SessionUser
import com.example.user.UserWithKey
import kotlinx.android.synthetic.main.search_list_item_user.view.*


class SearchAdapter(
    val context: Context,
    val resource : Int,
    val users : ArrayList<UserWithKey>
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        return ViewHolder(view)
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

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val name = view.full_name
        val desc = view.user_name
        val photo = view.profile_photo
    }

}
