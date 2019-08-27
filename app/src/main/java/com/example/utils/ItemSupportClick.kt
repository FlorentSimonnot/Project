package com.example.utils

import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.RecyclerView

class ItemSupportClick(
    val recyclerView: RecyclerView,
    val itemID: Int
) {


    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var onItemLongClickListener: OnItemLongClickListener

    fun config(){
        recyclerView.setTag(itemID, this)
        recyclerView.addOnChildAttachStateChangeListener(attachListener)
    }

    private val onClickListener = OnClickListener{

        fun onClick(v : View) {
            if (onItemClickListener != null) {
                val holder = recyclerView.getChildViewHolder(v)
                onItemClickListener.onItemClicked(recyclerView, holder.adapterPosition, v)
            }
        }
    }

    private val onLongClickListener = View.OnLongClickListener { v ->

        if (onItemLongClickListener!= null) {
            val holder = recyclerView.getChildViewHolder(v)
            return@OnLongClickListener onItemLongClickListener.onItemLongClicked(
                recyclerView,
                holder.adapterPosition,
                v
            )
        }
        false
    }

    private val attachListener = object : RecyclerView.OnChildAttachStateChangeListener {

        override fun onChildViewAttachedToWindow(view: View) {
            if (onItemClickListener != null) {
                view.setOnClickListener(onClickListener)
            }
            if (onItemClickListener != null) {
                view.setOnLongClickListener(onLongClickListener)
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {

        }
    }


    fun addTo(view: RecyclerView, itemID: Int): ItemSupportClick {
        var support: ItemSupportClick? = view.getTag(itemID) as ItemSupportClick
        if (support == null) {
            support = ItemSupportClick(view, itemID)
        }
        return support
    }

    fun removeFrom(view: RecyclerView, itemID: Int): ItemSupportClick? {
        val support = view.getTag(itemID) as ItemSupportClick
        if (support != null) {
            support!!.detach(view)
        }
        return support
    }

    fun setOnItemClickListener(listener: OnItemClickListener):ItemSupportClick {
        onItemClickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener): ItemSupportClick {
        onItemLongClickListener = listener
        return this
    }

    private fun detach(view: RecyclerView) {
        view.removeOnChildAttachStateChangeListener(attachListener)
        view.setTag(itemID, null)
    }


    interface OnItemClickListener {

        fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View)
    }

    interface OnItemLongClickListener {

        fun onItemLongClicked(recyclerView: RecyclerView, position: Int, v: View): Boolean
    }

}