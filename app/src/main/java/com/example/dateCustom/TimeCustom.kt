package com.example.dateCustom

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeCustom {
    var hour : Int = 0
    var min : Int = 0

    constructor(date : String){
        val listTime : List<String> = date.split(":")
        if(listTime.size != 2){
            throw Exception("Error format Time")
        }
        else{
            hour = listTime[0].toInt()
            min = listTime[1].toInt()
        }
    }

    override fun toString(): String {
        var strHour = ""
        var strMin = ""
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
        return "${strHour}:$strMin"
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
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val time = current.format(formatter)
        return TimeCustom(time)
    }
}