package com.example.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.dateCustom.TimeCustom
import com.example.session.SessionUser
import com.example.user.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_item_last_message.view.*
import kotlinx.android.synthetic.main.search_list_item_user.view.profile_photo
import org.w3c.dom.Text

class LatestMessageAdapter(
    val context: Context,
    val resource : Int,
    val messages: ArrayList<Message>,
    val onItemListener: OnItemListener
) : RecyclerView.Adapter<LatestMessageAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        return ViewHolder(view, onItemListener)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(messages[position].sender == SessionUser().getIdFromUser()){
            User().showPhotoUser(context, holder.photo, messages[position].addressee)
            User().writeIdentity(holder.name, messages[position].addressee)
            //SessionUser().writeInfoUser(context, messages[position].addressee, holder.name, "identity")
            if(messages[position].typeMessage == TypeMessage.TEXT)
                holder.message.text = "You : ${messages[position].text}"
            else{
                holder.message.text = "You send an image"
            }
        }else {
            //SessionUser().writeInfoUser(context, messages[position].addressee, holder.name, "identity")
            User().showPhotoUser(context, holder.photo, messages[position].sender)
            User().writeIdentity(holder.name, messages[position].sender)
            if(messages[position].typeMessage == TypeMessage.TEXT)
                holder.message.text = "${messages[position].text}"
            else{
                holder.message.text = "send an image"
            }
        }
        holder.time.text = messages[position].writeDate()
    }


    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener{

        var name : TextView
        var message : TextView
        var photo : CircleImageView
        var time : TextView
        var onItemListener : OnItemListener

        override fun onClick(p0: View?) {
            onItemListener.onClick(adapterPosition)
        }

        constructor(view : View, onItemListener: OnItemListener) : super(view) {
            name = view.name
            message = view.message
            photo = view.profile_photo
            time = view.time
            this.onItemListener = onItemListener

            view.setOnClickListener(this)
        }



    }

    interface OnItemListener{
        fun onClick(position : Int)
    }
}