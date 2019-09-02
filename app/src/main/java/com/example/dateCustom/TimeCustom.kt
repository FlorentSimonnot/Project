package com.example.dateCustom

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeCustom {
    var hour : Long = 0
    var min : Long = 0
    var sec : Long = 0
    var gmt : Long = 0

    constructor(date : String, gmt : Long = 0){
        val listTime : List<String> = date.split(":")
        this.gmt = gmt
        when(listTime.size){
            2 -> {
                hour = listTime[0].toLong() + gmt
                min = listTime[1].toLong()
                sec = 0
            }
            3 -> {
                hour = listTime[0].toLong() + gmt
                min = listTime[1].toLong()
                sec = listTime[2].toLong()
            }
            else -> {
                throw Exception("Error format Time")
            }
        }
    }

    override fun toString(): String {
        var strHour = ""
        var strMin = ""
        var strSec = ""
        if(hour < 10){
            strHour = "0$hour"
        }
        else{
            strHour = "$hour"
        }
        if(min < 10){
            strMin = "0$min"
        }
        else{
            strMin = "$min"
        }
        if(sec < 0){
            strSec = "0$sec"
        }
        else{
            strSec = "$sec"
        }
        return "$strHour:$strMin:$strSec"
    }

    fun showTime(showSeconds : Boolean = false) : String{
        if(showSeconds == true){
            return this.toString()
        }

        var strHour : String = if(hour < 10){
            "0$hour"
        } else{
            "$hour"
        }

        var strMin : String = if(min < 10){
            "0$min"
        } else{
            "$min"
        }

        return "$strHour:$strMin"
    }

    fun isAfter(time : TimeCustom) : Boolean{
        if(hour < time.hour){
            return false
        }
        else{
            if(hour == time.hour){
                if(min > time.min){
                    return true
                }
                return false
            }
            else{
                return true
            }
        }
    }

    fun isEqual(time : TimeCustom) : Boolean{
        return  hour == time.hour &&
                min == time.min
    }

    fun getCurrentTime() : TimeCustom{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val time = current.format(formatter)
        return TimeCustom(time)
    }
}