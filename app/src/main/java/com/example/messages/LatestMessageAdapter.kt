package com.example.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dateCustom.TimeCustom
import com.example.session.SessionUser
import com.example.user.User
import kotlinx.android.synthetic.main.list_item_last_message.view.*
import kotlinx.android.synthetic.main.search_list_item_user.view.profile_photo

class LatestMessageAdapter(
    val context: Context,
    val resource : Int,
    val messages: ArrayList<Message>
) : RecyclerView.Adapter<LatestMessageAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(messages[position].sender == SessionUser().getIdFromUser()){
            User().showPhotoUser(context, holder.photo, messages[position].addressee)
            SessionUser().writeInfoUser(context, messages[position].addressee, holder.name, "identity")
            if(messages[position].typeMessage == TypeMessage.TEXT)
                holder.message.text = "You : ${messages[position].text}"
            else{
                holder.message.text = "You send an image"
            }
        }else {
            User().showPhotoUser(context, holder.photo, messages[position].sender)
            SessionUser().writeInfoUser(context, messages[position].addressee, holder.name, "identity")
            if(messages[position].typeMessage == TypeMessage.TEXT)
                holder.message.text = "${messages[position].text}"
            else{
                holder.message.text = "send an image"
            }
        }
        holder.time.text = messages[position].writeDate()
    }


    open class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val name = view.name
        val message = view.message
        val photo = view.profile_photo
        val time = view.time
    }
}