package com.example.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.calendar.Calendar
import com.example.calendar.CalendarViewCustom
import java.lang.Exception
import kotlin.math.abs

class SwipeListenerCalendar(val context: Context, val calendar : CalendarViewCustom) : View.OnTouchListener {

    private val gestureDetector: GestureDetector = GestureDetector(context, GestureListener(context, calendar))

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private class GestureListener(val context: Context, val calendar : CalendarViewCustom) : GestureDetector.SimpleOnGestureListener(){


        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1 : MotionEvent, e2 : MotionEvent, velocityX : Float, velocityY : Float) : Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                }
                else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                    result = true
                }
            } catch (exception : Exception) {
                exception.printStackTrace()
            }
            return result
        }

        fun onSwipeRight() {
            val c = Calendar()
            c.setDate(1, calendar.selectedDate.month, calendar.selectedDate.year)
            if(!calendar.isCurrentMonth(c)){
                calendar.decreaseMonth()
                c.setDate(calendar.selectedDate.day, calendar.selectedDate.month, calendar.selectedDate.year)
                println("c ${calendar.selectedDate}")
                if(!calendar.isCurrentMonth(c)) {
                    calendar.previousMonth(
                        calendar.selectedDate.year,
                        calendar.selectedDate.month,
                        1
                    )
                }else{
                    calendar.selectedDate.day = c.day
                    calendar.previousMonth(
                        calendar.selectedDate.year,
                        calendar.selectedDate.month,
                        calendar.selectedDate.day
                    )
                }
            }
        }

        fun onSwipeLeft() {
            calendar.incrementMonth()
            calendar.nextMonth(
                calendar.selectedDate.year,
                calendar.selectedDate.month,
                1
            )
        }

        fun onSwipeTop() {}

        fun onSwipeBottom() {}
    }
}