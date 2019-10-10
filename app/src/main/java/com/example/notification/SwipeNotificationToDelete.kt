package com.example.notification

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.discussion.NotificationsAdapter
import com.example.project.R



class SwipeNotificationToDelete(
    val context: Context,
    val adapter : NotificationsAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {

    private val iconDrawable : Drawable = context.resources.getDrawable(R.drawable.ic_trash_notif)
    private val colorBackground : ColorDrawable = ColorDrawable(context.resources.getColor(R.color.deleteNotification))

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        //Nothing
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.deleteItem(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20 //so background is behind the rounded corners of itemView

        val iconMargin = (itemView.height - iconDrawable.intrinsicHeight) / 2
        val iconDrawableTop = itemView.top + (itemView.height - iconDrawable.intrinsicHeight) / 2
        val iconDrawableBottom = iconDrawableTop + iconDrawable.intrinsicHeight

        if (dX > 0) { // Swiping to the right
            val iconDrawableLeft = itemView.left + iconMargin
            val iconDrawableRight = iconDrawableLeft +  iconDrawable.intrinsicWidth
            iconDrawable.setBounds(iconDrawableLeft, iconDrawableTop, iconDrawableRight, iconDrawableBottom)

            colorBackground.setBounds(
                itemView.left, itemView.top,
                itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
            )
        } else if (dX < 0) { // Swiping to the left
            val iconDrawableLeft = itemView.right - iconMargin - iconDrawable.intrinsicWidth
            val iconDrawableRight = itemView.right - iconMargin
            iconDrawable.setBounds(iconDrawableLeft, iconDrawableTop, iconDrawableRight, iconDrawableBottom)

            colorBackground.setBounds(
                itemView.right + dX.toInt() - backgroundCornerOffset,
                itemView.top, itemView.right, itemView.bottom
            )
        } else { // view is unSwiped
            colorBackground.setBounds(0, 0, 0, 0)
        }

        colorBackground.draw(c)
        iconDrawable.draw(c)
    }
}