package com.example.discussion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notification.Notification
import com.example.project.R

class NotificationsAdapter(
    val context: Context,
    val resource : Int,
    val notifications: ArrayList<Notification>,
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
        println("HOLDER ${notifications[position].message}")
        holder.title.text = notifications[position].message
        holder.date.text = notifications[position].date
    }


    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener{

        var title : TextView
        var date : TextView
        var onItemListener : OnItemListener

        override fun onClick(p0: View?) {
            onItemListener.onClick(adapterPosition)
        }

        constructor(view : View, onItemListener: OnItemListener) : super(view) {
            title = view.findViewById(R.id.title)
            date = view.findViewById(R.id.date)
            this.onItemListener = onItemListener

            view.setOnClickListener(this)
        }



    }

    interface OnItemListener{
        fun onClick(position : Int)
    }
}