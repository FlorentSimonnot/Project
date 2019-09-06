package com.example.discussion

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dateCustom.DateUTC
import com.example.events.Event
import com.example.notification.Action
import com.example.notification.Notification
import com.example.notification.NotificationWithKey
import com.example.notification.TypeNotification
import com.example.project.R
import com.example.user.User
import java.util.*
import kotlin.collections.ArrayList

class NotificationsAdapter(
    val context: Context,
    val resource : Int,
    val notifications: ArrayList<NotificationWithKey>,
    val onItemListener: OnItemListener
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>(){


    fun clear(){
        val size = notifications.size
        notifications.clear()
        this.notifyDataSetChanged()
        this.notifyItemRangeRemoved(0, size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        return ViewHolder(view, onItemListener)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var date = DateUTC(notifications[position].notification.date.toLong())
        if(date.isToday())
            holder.date.text = date.showTime()
        else{
            holder.date.text = date.showDate()
        }
        holder.title.text = Html.fromHtml(notifications[position].notification.message)
        if(notifications[position].notification.type.typeNotif == TypeNotification.EVENT){
            Event().showPhotoCreatorEvent(context, notifications[position].notification.type.key, holder.image)
            //Event().writeNotificationEvent(context, notifications[position].notification.type.key, holder.title, notifications[position].notification.type.action)
        }
        else{
            User().showPhotoUser(context, holder.image, notifications[position].notification.type.key)
            User().writeBulletNotificationUser(context, holder.bullet, notifications[position].notification.type.action)
        }
        colorizeItem(position, holder)

    }


    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener, View.OnLongClickListener{
        var content : LinearLayout
        var title : TextView
        var date : TextView
        var image : ImageView
        var bullet : ImageView
        var onItemListener : OnItemListener

        override fun onClick(p0: View?) {
            onItemListener.onClick(adapterPosition)
        }

        override fun onLongClick(p0: View?): Boolean {
            onItemListener.onLongClick(adapterPosition)
            return true
        }


        constructor(view : View, onItemListener: OnItemListener) : super(view) {
            content = view.findViewById(R.id.content)
            title = view.findViewById(R.id.title)
            date = view.findViewById(R.id.date)
            image = view.findViewById(R.id.profile_photo)
            bullet = view.findViewById(R.id.bullet)
            this.onItemListener = onItemListener

            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }



    }

    private fun colorizeItem(position: Int, holder: ViewHolder){
        if(!notifications[position].notification.isSeen){
            holder.content.setBackgroundResource(R.drawable.background_notif_not_seen)
        }
    }

    interface OnItemListener{
        fun onClick(position : Int)
        fun onLongClick(position: Int)
    }
}