package com.example.dateCustom

import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class DateUTC(
    val date : Long
) {

    fun showDate() : String{
        val format = "dd/MM/yyyy"
        val simpleDate = SimpleDateFormat(format)
        return simpleDate.format(date)
    }

    fun showTime(showSeconds : Boolean = false) : String{
        if(showSeconds){
            val format = "HH:mm:ss"
            val simpleDate = SimpleDateFormat(format)
            return simpleDate.format(date)
        }
        val format = "HH:mm"
        val simpleDate = SimpleDateFormat(format)
        return simpleDate.format(date)
    }

    fun isToday() : Boolean{
        val dateCustom = DateCustom(SimpleDateFormat("dd/MM/yyyy").format(date))
        val dateToday = DateCustom(SimpleDateFormat("dd/MM/yyyy").format(Date().time))
        return dateCustom.isEqual(dateToday)
    }

    fun isYesterday() : Boolean{
        val dateCustom = DateCustom(SimpleDateFormat("dd/MM/yyyy").format(date))
        return dateCustom.isYesterday()
    }

}