package com.example.dateCustom

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateCustom {
    var day : Int = 0
    var month : Int = 0
    var year : Int = 0

    constructor(date : String){
        val listDate : List<String> = date.split("/")
        if(listDate.size != 3){
            throw Exception("Error format Date")
        }
        else{
            day = listDate[0].toInt()
            month = listDate[1].toInt()
            year = listDate[2].toInt()
        }
    }

    override fun toString(): String {
        var strDay = ""
        var strMonth = ""
        if(day < 10){
            strDay = "0$day"
        }
        if(month < 10){
            strMonth = "0$month"
        }
        return "$strDay/$strMonth/$year"
    }

    fun isAfter(date : DateCustom) : Boolean{
        if(year < date.year){
            return false
        }
        else if(year == date.year){
            if(month < date.month){
                return false
            }
            else if(month == date.month){
                if(day > date.day){
                    return true
                }
                return false
            }
            return true
        }
        return true
    }

    fun isEqual(date : DateCustom) : Boolean{
        return  day == date.day &&
                month == date.month &&
                year == date.year
    }

    fun getCurrentDate() : DateCustom{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = current.format(formatter)
        return DateCustom(date)
    }

}