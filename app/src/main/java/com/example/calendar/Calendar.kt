package com.example.calendar

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

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

    fun setDate(day : Int, month : Int, year : Int){
        this.day = day
        this.month = month
        this.year = year
    }

    fun getMonth() : String{
        val date = Calendar.getInstance()
        date.set(year, month, day)
        return SimpleDateFormat("MMM").format(date.time).toString()
    }

    fun showMonthAndYear() : String{
        val dateString = if(month < 10){"$day/0$month/$year"}else{"$day/$month/$year"}
        val date = SimpleDateFormat("dd/MM/yyyy").parse(dateString)
        return SimpleDateFormat("MMMM yyyy").format(date)
    }

    override fun toString(): String {
        return "$day - $month - $year"
    }

    override fun equals(other: Any?): Boolean {
        if (other?.javaClass != javaClass) return false
        val c = other as com.example.calendar.Calendar
        return c.day == day && c.month == month && c.year == year
    }

}