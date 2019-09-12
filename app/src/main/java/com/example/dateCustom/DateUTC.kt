package com.example.dateCustom

import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class DateUTC(
    val date : Long
) {

    private fun getFormatDate() : SimpleDateFormat{
        if(Locale.getDefault().displayLanguage == "한국어"){
            return SimpleDateFormat("yyyy.MM.dd")
        }
        return SimpleDateFormat("dd/MM/yyyy")
    }

    private fun getFormatTime() : SimpleDateFormat{
        if(Locale.getDefault().displayLanguage == "한국어"){
            return SimpleDateFormat("hh:mm a")
        }
        return SimpleDateFormat("HH:mm")
    }

    fun showDate() : String{
        val simpleDate = getFormatDate()
        return simpleDate.format(date)
    }

    fun showTime(showSeconds : Boolean = false) : String{
        if(showSeconds){
            val format = "HH:mm:ss"
            val simpleDate = SimpleDateFormat(format)
            return simpleDate.format(date)
        }
        val simpleDate = getFormatTime()
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

    fun isAfterNow() : Boolean{
        var now = Date().time
        return date >= now
    }

}