package com.example.dateCustom

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeCustom {
    var hour : Int = 0
    var min : Int = 0
    var sec : Int = 0

    constructor(date : String){
        val listTime : List<String> = date.split(":")
        if(listTime.size != 3){
            throw Exception("Error format Time")
        }
        else{
            hour = listTime[0].toInt()
            min = listTime[1].toInt()
            sec = listTime[2].toInt()
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