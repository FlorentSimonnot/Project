package com.example.calendar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.horizontal_calendar_item.view.*
import android.R
import android.os.Handler
import android.view.LayoutInflater
import android.os.Looper


class HorizontalCalendarAdapter(
    val context: Context,
    val resources : Int,
    var data : ArrayList<HorizontalCalendarItem>,
    val recyclerView: RecyclerView,
    val mDaySelectedListener: OnDaySelectedListener,
    val mOnEndReachedListener : OnSideReachedListener,
    var mIsFirstBind : Boolean = true
) : RecyclerView.Adapter<HorizontalCalendarAdapter.ViewHolder>() {
    private val bottomAdvanceCallback = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(resources, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0 && !mIsFirstBind) {
            notifyEndReached()
        } else if (position + bottomAdvanceCallback >= itemCount - 1) {
            notifyStartReached()
        }

        mIsFirstBind = false
    }

    fun addItemsAtBottom(bottomList: List<HorizontalCalendarItem>?) {
        if (data == null) {
            throw NullPointerException("Data container is `null`. Are you missing a call to setDataContainer()?")
        }

        if (bottomList == null || bottomList.isEmpty()) {
            return
        }

        val adapterSize = itemCount
        data.addAll(adapterSize, bottomList)
        notifyItemRangeInserted(adapterSize, adapterSize + bottomList.size)
    }

    fun addItemsAtTop(topList: List<HorizontalCalendarItem>?) {
        if (data == null) {
            throw NullPointerException("Data container is `null`. Are you missing a call to setDataContainer()?")
        }

        if (topList == null || topList.isEmpty()) {
            return
        }

        data.addAll(0, topList)
        notifyItemRangeInserted(0, topList.size)
    }

    private fun notifyEndReached() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ mOnEndReachedListener?.onEndReached() }, 50)
    }

    private fun notifyStartReached() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ mOnEndReachedListener?.onStartReached() }, 50)
    }

    open class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val day = view.calendar_day
        val dateLayout = view.date_layout
        val itemLayout = view.calendar_item
    }

    interface OnDaySelectedListener {
        fun OnDaySelected(view: View, date: String, position: Int)
    }

    interface OnSideReachedListener {
        fun onEndReached()
        fun onStartReached()
    }

    fun setValue(array: ArrayList<HorizontalCalendarItem>) {
        data = array
    }
}