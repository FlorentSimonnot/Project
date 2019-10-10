package com.example.calendar

import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class Calendar {
    var day : Int = 0
    var month : Int = 0
    var year : Int = 0

    init{
        val date = Date().time
        val formaterDay = SimpleDateFormat("dd")
        val formaterMonth = SimpleDateFormat("MM")
        val formaterYear = SimpleDateFormat("yyyy")

        day = formaterDay.format(date).toString().toInt()
        month = formaterMonth.format(date).toString().toInt()
        year = formaterYear.format(date).toString().toInt()
    }

    fun showMonthAndYear() : String{
        val dateString = if(month < 10){"$day/0$month/$year"}else{"$day/$month/$year"}
        val date = SimpleDateFormat("dd/MM/yyyy").parse(dateString)
        return SimpleDateFormat("MMMM yyyy").format(date)
    }

    fun setNextMonth(){
        if(month == 12){
            month = 1
            year++
        }
        else{
            month++
        }
    }

    fun setPreviousMonth(){
        if(month == 1){
            month = 12
            year--
        }
        else{
            month--
        }
    }

    fun showMonthYear(textView: TextView){
        textView.text = showMonthAndYear()
    }

}