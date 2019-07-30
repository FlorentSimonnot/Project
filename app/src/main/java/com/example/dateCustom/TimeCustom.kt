package com.example.dateCustom

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

}