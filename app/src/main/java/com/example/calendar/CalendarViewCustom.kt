package com.example.calendar

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.project.R
import kotlinx.android.synthetic.main.activity_group_information_event_discussion.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import kotlin.collections.ArrayList

class CalendarViewCustom(
    val view: View,
    val context : Context
) : View.OnClickListener{
    private var calendar : Calendar = Calendar.getInstance(TimeZone.getDefault())
    var currentDate : com.example.calendar.Calendar = Calendar()
    private lateinit var listWeeks : ArrayList<LinearLayout>
    lateinit var listDays : ArrayList<Button>
    private lateinit var listDaysName : ArrayList<Button>
    private var monthTextView : TextView = view.findViewById(R.id.monthTextView)
    private var pickedDate : com.example.calendar.Calendar = currentDate
    private var selectButton : Button? = null

    override fun onClick(v: View?) {
        val button = v as Button
        Toast.makeText(context, "${button.text}", Toast.LENGTH_LONG).show()
    }

    fun init(){
        initWeeks(view)
        writeDaysName(view)
        initDays()
        currentDate.month = calendar.get(Calendar.MONTH)
        currentDate.year = calendar.get(Calendar.YEAR)
        currentDate.day = calendar.get(Calendar.DAY_OF_MONTH)
        initCalendarWithDate(currentDate.year, currentDate.month, currentDate.day)
        writeCurrentMonth()
    }

    private fun initWeeks(view: View){
        val weekOne = view.findViewById<LinearLayout>(R.id.calendar_week_1)
        val weekSecond = view.findViewById<LinearLayout>(R.id.calendar_week_2)
        val weekThird = view.findViewById<LinearLayout>(R.id.calendar_week_3)
        val weekFourth = view.findViewById<LinearLayout>(R.id.calendar_week_4)
        val weekFifth = view.findViewById<LinearLayout>(R.id.calendar_week_5)
        val weekSixth = view.findViewById<LinearLayout>(R.id.calendar_week_6)

        listWeeks = ArrayList()
        listWeeks.add(weekOne); listWeeks.add(weekSecond); listWeeks.add(weekThird)
        listWeeks.add(weekFourth); listWeeks.add(weekFifth); listWeeks.add(weekSixth)
    }

    private fun initDays(){
        listDays = ArrayList()
        val buttonParams = LinearLayout.LayoutParams(120, 120)
        buttonParams.weight = 1.0f

        var daysArrayCount = 0
        for (weekNumber in 0 until 6) {
            for (dayInWeek in 0 until 7) {
                val day = Button(context)
                day.setTextColor(Color.parseColor("#dedede"))
                day.setBackgroundColor(Color.TRANSPARENT)
                //day.background = context.resources.getDrawable(R.drawable.rounded_button)
                day.setPadding(15, 15, 15,15)
                day.layoutParams = buttonParams
                day.textSize = context.resources.displayMetrics.density * 8
                val listener = View.OnClickListener {
                    onClick(it)
                }
                day.setOnClickListener(listener)
                day.setSingleLine()
                println("$dayInWeek AND $weekNumber ${day.hasOnClickListeners()}")

                listDays.add(day)
                listWeeks[weekNumber].addView(day)

                ++daysArrayCount
            }
        }
    }

    private fun initCalendarWithDate(year : Int, month : Int, day : Int){
        val c = Calendar.getInstance()

        val currentMonth = month
        val currentYear = year
        val currentDay = day

        if(c.get(Calendar.MONTH) != currentMonth)
            c.set(currentYear, currentMonth+1, currentDay)

        val maxDayInMonth = c.getActualMaximum(Calendar.DATE)
        c.set(currentYear, currentMonth, 1)

        var firstDayInMonth = c.get(Calendar.DAY_OF_WEEK)-2
        if(firstDayInMonth == -1){
            firstDayInMonth = 6
        }

        c.set(currentYear, currentMonth, maxDayInMonth)

        var dayNumber = 1
        val daysLeftInFirstWeek: Int
        val indexOfDayAfterLastDayOfMonth : Int

        if(firstDayInMonth != 0) {
            daysLeftInFirstWeek = firstDayInMonth
            indexOfDayAfterLastDayOfMonth = daysLeftInFirstWeek + maxDayInMonth
            for (i in firstDayInMonth until (firstDayInMonth + maxDayInMonth)) {
                if (currentDate.month == currentMonth && currentDate.year == currentYear && dayNumber == currentDay) {
                    listDays[i].background = context.resources.getDrawable(R.drawable.border_bottom_red)
                    listDays[i].setTextColor(Color.RED)
                } else {
                    listDays[i].setTextColor(Color.WHITE)
                    listDays[i].setBackgroundColor(Color.TRANSPARENT)
                    //listDays[i].background = context.resources.getDrawable(R.drawable.border_bottom_red)
                }
                val intArray = intArrayOf(dayNumber, currentMonth, currentYear)
                listDays[i].tag = intArray
                listDays[i].text = dayNumber.toString()
                dayNumber++
            }
        }
        else{
            daysLeftInFirstWeek = 7
            indexOfDayAfterLastDayOfMonth = daysLeftInFirstWeek + maxDayInMonth
            for (i in 7 until 7+maxDayInMonth) {
                if (currentDate.month == currentMonth && currentDate.year == currentYear && dayNumber == currentDay) {
                    //listDays[i].setBackgroundColor(context.resources.getColor(R.color.colorAccent))
                    //listDays[i].background = context.resources.getDrawable(R.drawable.circle_red)
                    listDays[i].background = context.resources.getDrawable(R.drawable.border_bottom_red)
                    listDays[i].setTextColor(Color.RED)
                } else {
                    listDays[i].setTextColor(Color.WHITE)
                    listDays[i].setBackgroundColor(Color.TRANSPARENT)
                }

                val intArray = intArrayOf(dayNumber, currentMonth, currentYear)
                listDays[i].tag = intArray
                listDays[i].text = dayNumber.toString()
                ++dayNumber
            }
        }

        if (month > 0)
            c.set(year, month - 1, 1)
        else
            c.set(year - 1, 11, 1)

        var  daysInPreviousMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in (daysLeftInFirstWeek - 1) downTo 0) {

            if (currentMonth > 0) {
                if (currentDate.month == currentMonth - 1 && currentDate.year == currentYear && daysInPreviousMonth == currentDay) {
                    listDays[i].setTextColor(context.resources.getColor(R.color.quantum_grey100))
                } else {
                    listDays[i].setBackgroundColor(Color.TRANSPARENT)
                }

                listDays[i].tag = intArrayOf(daysInPreviousMonth, currentMonth, currentYear)
            }
            else {
                if (currentDate.month == 11 && currentDate.year == currentYear- 1 && daysInPreviousMonth == currentDay) {
                    listDays[i].setTextColor(context.resources.getColor(R.color.quantum_grey100))
                } else {
                    listDays[i].setBackgroundColor(Color.TRANSPARENT);
                }

                listDays[i].tag = intArrayOf(daysInPreviousMonth, 11, currentYear-1)
            }

            listDays[i].setTextColor(context.resources.getColor(R.color.quantum_black_secondary_text))
            listDays[i].text = (daysInPreviousMonth--).toString()
        }

        var nextMonthDaysCounter = 1
        for (i in indexOfDayAfterLastDayOfMonth until listDays.size) {

            if (currentMonth < 11) {
                if (currentDate.month == currentMonth + 1 && currentDate.year == currentYear && nextMonthDaysCounter == currentDay) {
                }
                else {
                    listDays[i].setBackgroundColor(Color.TRANSPARENT)
                }
                val intArray = intArrayOf(nextMonthDaysCounter, currentMonth+1, currentYear)
                listDays[i].tag = intArray
            }
            else {
                if (currentDate.month == 0 && currentDate.year == currentYear + 1 && nextMonthDaysCounter == currentDay) {
                } else {
                    listDays[i].setBackgroundColor(Color.TRANSPARENT)
                }
                val intArray = intArrayOf(nextMonthDaysCounter, 0, currentYear+1)
                listDays[i].tag = intArray
            }

            listDays[i].setTextColor(context.resources.getColor(R.color.quantum_black_secondary_text))
            listDays[i].text = (nextMonthDaysCounter++).toString()
        }

        c.set(currentYear, currentMonth, currentDay)
    }

    fun writeCurrentMonth(){
        val c = Calendar.getInstance()
        c.set(currentDate.year, currentDate.month, currentDate.day)
        monthTextView.text = SimpleDateFormat("MMMM YYYY").format(c.timeInMillis).toUpperCase()
    }

    private fun writeDaysName(view : View){
        val dayOne = view.findViewById<Button>(R.id.firstDay)
        val dayTwo = view.findViewById<Button>(R.id.SecondDay)
        val dayThree = view.findViewById<Button>(R.id.ThirdDay)
        val dayFour = view.findViewById<Button>(R.id.FourthDay)
        val dayFive = view.findViewById<Button>(R.id.FifthDay)
        val daySix = view.findViewById<Button>(R.id.SixthDay)
        val daySeven = view.findViewById<Button>(R.id.SeventhDay)

        listDaysName = ArrayList()
        listDaysName.add(dayOne); listDaysName.add(dayTwo); listDaysName.add(dayThree)
        listDaysName.add(dayFour); listDaysName.add(dayFive); listDaysName.add(daySix); listDaysName.add(daySeven)

        val arrayDaysName = context.resources.getStringArray(R.array.days)

        if(Calendar.getInstance().firstDayOfWeek == Calendar.SUNDAY)
            listDaysName.forEachIndexed { index, button -> button.text = arrayDaysName[index] }
        else{
            listDaysName.forEachIndexed {
                    index, button -> if(index < 6) button.text = arrayDaysName[index+1] else{ button.text = arrayDaysName[0]}
            }
        }


    }

    fun previousMonth(){
        val c = Calendar.getInstance()
        if(currentDate.month == 0){
            currentDate.year--
            currentDate.month = 11
        }else{
            currentDate.month--
        }
        if(currentDate.year == c.get(Calendar.YEAR) && currentDate.month == c.get(Calendar.MONTH))
            initCalendarWithDate(currentDate.year, currentDate.month, currentDate.day)
        else
            initCalendarWithDate(currentDate.year, currentDate.month, -1)
        writeCurrentMonth()
    }

    fun nextMonth(){
        val c = Calendar.getInstance()
        if(currentDate.month == 11){
            currentDate.year++
            currentDate.month = 0
        }
        else{
            currentDate.month++
        }
        if(currentDate.year == c.get(Calendar.YEAR) && currentDate.month == c.get(Calendar.MONTH))
            initCalendarWithDate(currentDate.year, currentDate.month, currentDate.day)
        else
            initCalendarWithDate(currentDate.year, currentDate.month, -1)
        writeCurrentMonth()
    }

    private fun onDayClick(view : View){

        if(selectButton != null){
            selectButton!!.setBackgroundColor(Color.TRANSPARENT)
            selectButton!!.setTextColor(Color.WHITE)
        }

        selectButton = view as Button
        println("NULL ${selectButton==null}")
        selectButton!!.setBackgroundColor(Color.RED)
        selectButton!!.setTextColor(Color.RED)
    }

}

