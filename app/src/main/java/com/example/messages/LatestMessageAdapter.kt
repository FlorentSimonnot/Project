package com.example.discussion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.dateCustom.TimeCustom
import com.example.messages.DiscussionViewLastMessage
import com.example.messages.TypeMessage
import com.example.session.SessionUser
import com.example.user.User
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_item_last_message.view.*
import kotlinx.android.synthetic.main.search_list_item_user.view.profile_photo
import org.w3c.dom.Text

class LatestMessageAdapter(
    val context: Context,
    val resource : Int,
    val discussion: ArrayList<DiscussionViewLastMessage>,
    val onItemListener: OnItemListener
) : RecyclerView.Adapter<LatestMessageAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        return ViewHolder(view, onItemListener)
    }

    override fun getItemCount(): Int {
        return discussion.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(discussion[position].lastMessage.sender.key == SessionUser().getIdFromUser()){
            User().showPhotoUser(context, holder.photo, discussion[position].lastMessage.addressee.key)
            User().writeIdentity(holder.name, discussion[position].lastMessage.addressee.key)
            if(discussion[position].lastMessage.typeMessage == TypeMessage.TEXT)
                holder.message.text = "You : ${discussion[position].lastMessage.text}"
            else{
                holder.message.text = "You send an image"
            }
        }else {
            User().showPhotoUser(context, holder.photo, discussion[position].lastMessage.sender.key)
            User().writeIdentity(holder.name, discussion[position].lastMessage.sender.key)
            if(discussion[position].lastMessage.typeMessage == TypeMessage.TEXT)
                holder.message.text = "${discussion[position].lastMessage.text}"
            else{
                holder.message.text = "send an image"
            }
            discussion[position].discussionIsNotSeen(holder.message, holder.time, holder.see)
        }
        holder.time.text = discussion[position].lastMessage.writeDate()
    }


    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener{

        var name : TextView
        var message : TextView
        var photo : CircleImageView
        var time : TextView
        var see : RelativeLayout
        var onItemListener : OnItemListener

        override fun onClick(p0: View?) {
            onItemListener.onClick(adapterPosition)
        }

        constructor(view : View, onItemListener: OnItemListener) : super(view) {
            name = view.name
            message = view.message
            photo = view.profile_photo
            time = view.time
            see = view.see_circle
            this.onItemListener = onItemListener

            view.setOnClickListener(this)
        }



    }

    interface OnItemListener{
        fun onClick(position : Int)
    }
}