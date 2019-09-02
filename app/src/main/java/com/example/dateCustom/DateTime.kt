package com.example.dateCustom

class DateTime(
    val date : String = "",
    val gmt : Long = 0,
    val time : String = ""
) {


    override fun toString(): String {
        val dateCustom = DateCustom(date)
        val timeCustom = TimeCustom(time, gmt)

        if(dateCustom.toString() == DateCustom("00/00/0000").getCurrentDate().toString()){
            return timeCustom.showTime()
        }
        if(dateCustom.isYesterday()){
            return "Yesterday"
        }
        return dateCustom.toString()

    }

}