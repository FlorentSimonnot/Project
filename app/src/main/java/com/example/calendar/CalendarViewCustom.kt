package com.example.calendar

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.media.Image
import android.view.View
import android.widget.*
import androidx.core.view.setMargins
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.dateCustom.DateUTC
import com.example.events.Event
import com.example.events.EventWithDistance
import com.example.project.R
import com.example.utils.SwipeListenerCalendar
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import kotlin.collections.ArrayList

class CalendarViewCustom(
    val view: View,
    val context : Context,
    val activity : Activity,
    val loadingSpinner : RelativeLayout,
    val calendar: LinearLayout,
    val calendarButton: LinearLayout,
    val listView : ListView,
    val previousArrow : ImageButton,
    val nextArrow : ImageButton,
    val textDay : TextView,
    val textMonth : TextView,
    val distance : Long,
    val currentLatLng : LatLng
) : View.OnClickListener{
    private lateinit var currentDate : com.example.calendar.Calendar
    private lateinit var listWeeks : ArrayList<LinearLayout>
    private lateinit var listDays : ArrayList<Button>
    private lateinit var listDaysName : ArrayList<Button>
    private var events : ArrayList<EventWithDistance> = ArrayList()
    private var monthTextView : TextView = view.findViewById(R.id.monthTextView)
    private var selectButton : Button? = null
    lateinit var selectedDate : com.example.calendar.Calendar

    override fun onClick(v: View?) {
        onDayClick(v!!)
    }

    fun init(){
        currentDate = Calendar()
        selectedDate = currentDate
        initWeeks(view)
        writeDaysName(view)
        initDays()
        setCurrentDate()
        writeCurrentMonth()
        searchEvent(currentDate.year, currentDate.month, currentDate.day, true)
        calendarButton.visibility = View.VISIBLE
        loadingSpinner.visibility = View.GONE
        calendar.setOnTouchListener(SwipeListenerCalendar(context, this))
    }

    private fun setCurrentDate() {
        val calendar : Calendar = Calendar.getInstance(TimeZone.getDefault())
        currentDate.month = calendar.get(Calendar.MONTH)
        currentDate.year = calendar.get(Calendar.YEAR)
        currentDate.day = calendar.get(Calendar.DAY_OF_MONTH)
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

        listWeeks.forEach {
            it.removeAllViews()
        }

    }

    private fun initDays(){
        listDays = ArrayList()
        val buttonParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100, 1.0f)
        buttonParams.setMargins(18, 10, 18, 10)

        var daysArrayCount = 0
        for (weekNumber in 0 until 6) {
            for (dayInWeek in 0 until 7) {
                val day = Button(context)
                day.setTextColor(Color.parseColor("#dedede"))
                day.setBackgroundColor(Color.TRANSPARENT)
                day.layoutParams = buttonParams
                day.textSize = context.resources.displayMetrics.density * 5
                val listener = View.OnClickListener {
                    onClick(it)
                }
                day.setOnClickListener(listener)
                day.setSingleLine()

                listDays.add(day)
                listWeeks[weekNumber].addView(day)

                ++daysArrayCount
            }
        }
    }

    private fun initButtons(){
        listDays.forEach {
            it.setTextColor(Color.parseColor("#dedede"))
            it.setBackgroundColor(Color.TRANSPARENT)
            it.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun initCalendarWithDate(year : Int, month : Int, day : Int, events : ArrayList<com.example.calendar.Calendar>){
        val c = Calendar.getInstance()

        writeDateOnButton(year, month, day)
        initButtons()

        val tt = Calendar()
        tt.setDate(day, month, year)

        val currentMonth = month
        val currentYear = year
        val currentDay = if(isCurrentMonth(tt)){
            currentDate.day
        }else{
            day
        }

        c.set(currentYear, currentMonth, currentDay)
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
                if(dayNumber == currentDay){
                    val today = Calendar()
                    today.setDate(currentDay, currentMonth, currentYear)
                    if(isToday(today)){
                        listDays[i].background = context.resources.getDrawable(R.drawable.circle_red)
                        //listDays[i].setTextColor(Color.WHITE)
                    }
                    else {
                        listDays[i].background = context.resources.getDrawable(R.drawable.circle_blue)
                    }
                    selectButton = listDays[i]
                }
                else {
                    //listDays[i].setTextColor(Color.WHITE)
                    listDays[i].setBackgroundColor(Color.TRANSPARENT)
                }

                //Draw bullet if there are events at this day
                val calendar = Calendar()
                calendar.setDate(dayNumber, currentMonth, currentYear)
                if(events.contains(calendar)){
                    listDays[i].compoundDrawablePadding = 5
                    listDays[i].setCompoundDrawablesWithIntrinsicBounds(null, null, null, context.resources.getDrawable(R.drawable.circle_white))
                }

                //Set the tag for the button
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
                if(dayNumber == currentDay){
                    val today = Calendar()
                    today.setDate(currentDay, currentMonth, currentYear)
                    if(isToday(today)){
                        listDays[i].background = context.resources.getDrawable(R.drawable.circle_red)
                        //listDays[i].setTextColor(Color.WHITE)
                    }
                    else {
                        listDays[i].background = context.resources.getDrawable(R.drawable.circle_blue)
                    }
                    selectButton = listDays[i]
                }
                else {
                    //listDays[i].setTextColor(Color.WHITE)
                    listDays[i].setBackgroundColor(Color.TRANSPARENT)
                }

                //Draw bullet if there are events at this day
                val calendar = Calendar()
                calendar.setDate(dayNumber, currentMonth, currentYear)
                if(events.contains(calendar)){
                    listDays[i].setTextColor(Color.BLUE)
                    listDays[i].setCompoundDrawables(null, null, null, context.resources.getDrawable(R.drawable.circle_blue))
                }

                //Set the tag for the button
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

                listDays[i].tag = intArrayOf(daysInPreviousMonth, currentMonth-1, currentYear)
            }
            else {
                if (currentDate.month == 11 && currentDate.year == currentYear- 1 && daysInPreviousMonth == currentDay) {
                    listDays[i].setTextColor(context.resources.getColor(R.color.quantum_grey100))
                } else {
                    listDays[i].setBackgroundColor(Color.TRANSPARENT)
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

    }

    private fun writeCurrentMonth(){
        val c = Calendar.getInstance()
        c.set(selectedDate.year, selectedDate.month, 1)
        monthTextView.text = SimpleDateFormat("MMMM YYYY").format(c.time).toUpperCase()
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

        /*if(Calendar.getInstance().firstDayOfWeek == Calendar.SUNDAY)
            listDaysName.forEachIndexed { index, button -> button.text = arrayDaysName[index] }
        else{
            listDaysName.forEachIndexed {
                    index, button -> if(index < 6) button.text = arrayDaysName[index+1] else{ button.text = arrayDaysName[0]}
            }
        }*/
        listDaysName.forEachIndexed {
                index, button -> if(index < 6) button.text = arrayDaysName[index+1] else{ button.text = arrayDaysName[0]}
        }


    }

    /**
     * decreaseMonth()
     * change selected month to previous month
     */
    fun decreaseMonth(){
        if(selectedDate.month == 0){
            selectedDate.year--
            selectedDate.month = 11
        }else{
            selectedDate.month--
        }
    }

    /**
     * previousMonth(year : Int, month : Int, day : Int)
     * show the previous month
     * @param year - the year as Integer
     * @param month - the month as Integer
     * @param day - the day as Integer
     */
    fun previousMonth(year : Int, month : Int, day : Int){

        val date = Calendar()
        date.setDate(day, month, year)
        if(isCurrentMonth(date)){
            previousArrow.visibility = View.GONE
        }
        writeCurrentMonth()
        searchEvent(year, month, day)
    }

    /**
     * incrementMonth()
     * change selected month to next month
     */
    fun incrementMonth(){
        if(selectedDate.month == 11){
            selectedDate.year++
            selectedDate.month = 0
        }
        else{
            selectedDate.month++
        }
    }

    fun nextMonth(year : Int, month : Int, day : Int){

        //Show previous arrow if we are after the current month
        val date = Calendar()
        date.setDate(day, month, year)
        if(!isCurrentMonth(date)){
            previousArrow.visibility = View.VISIBLE
        }

        writeCurrentMonth()
        searchEvent(year, month, day)
    }

    private fun onDayClick(view : View){
        val tmp = view as Button

        if(!isPreviousMonth(getDatePicked(tmp), currentDate)){
            if(selectButton != null){
                selectButton!!.setBackgroundColor(Color.TRANSPARENT)
                selectButton!!.setTextColor(Color.WHITE)

                if(isToday(getDatePicked(selectButton!!))){
                    selectButton!!.background = context.resources.getDrawable(R.drawable.circle_transparent_border_white)
                }
            }

            selectButton = view
            val oldSelectedDate = selectedDate
            selectedDate = getDatePicked(selectButton!!)

            if(isToday(selectedDate)){
                if(isNextMonth(oldSelectedDate, selectedDate)){
                    previousMonth(selectedDate.year, selectedDate.month, selectedDate.day)
                }
                else {
                    selectButton!!.background = context.resources.getDrawable(R.drawable.circle_red)
                    writeDateOnButton(selectedDate.year, selectedDate.month, selectedDate.day)
                    searchEvent(selectedDate.year, selectedDate.month, selectedDate.day, false)
                }
            }else{
                if(isNextMonth(selectedDate, oldSelectedDate)){
                    nextMonth(selectedDate.year, selectedDate.month, selectedDate.day)
                }
                else if(isPreviousMonth(selectedDate, oldSelectedDate)){
                    previousMonth(selectedDate.year, selectedDate.month, selectedDate.day)
                }
                else {
                    selectButton!!.background = context.resources.getDrawable(R.drawable.circle_blue)
                    selectButton!!.setTextColor(Color.WHITE)
                    writeDateOnButton(selectedDate.year, selectedDate.month, selectedDate.day)
                    searchEvent(selectedDate.year, selectedDate.month, selectedDate.day, false)
                }
            }
            calendar.visibility = View.GONE
        }

    }

    private fun isNextMonth(calendar: com.example.calendar.Calendar, oldCalendar: com.example.calendar.Calendar) : Boolean{
        return calendar.month == oldCalendar.month+1 || (calendar.month == 0 && calendar.year == oldCalendar.year+1)
    }

    private fun isPreviousMonth(calendar: com.example.calendar.Calendar, oldCalendar: com.example.calendar.Calendar) : Boolean{
        return calendar.month == oldCalendar.month-1 || (calendar.month == 11 && calendar.year == oldCalendar.year-1)
    }

    private fun isToday(calendar: com.example.calendar.Calendar) : Boolean{
        return calendar.day == Calendar.getInstance().get(Calendar.DATE) &&
                calendar.month == Calendar.getInstance().get(Calendar.MONTH) &&
                calendar.year == Calendar.getInstance().get(Calendar.YEAR)
    }

    fun isCurrentMonth(calendar: com.example.calendar.Calendar) : Boolean{
        return calendar.month == Calendar.getInstance().get(Calendar.MONTH) &&
                calendar.year == Calendar.getInstance().get(Calendar.YEAR)
    }

    private fun getDatePicked(button: Button) : com.example.calendar.Calendar{
        val dateArray = button.tag as IntArray
        val calendar = Calendar()
        calendar.setDate(dateArray[0], dateArray[1], dateArray[2])
        return calendar
    }

    private fun writeDateOnButton(year : Int, month : Int, day : Int){
        val date = Calendar()
        date.setDate(day, month, year)
        textDay.text = date.day.toString()
        textMonth.text = date.getMonth()
    }

    private fun isSelectedDay(calendar: com.example.calendar.Calendar) : Boolean{
        return calendar.day == selectedDate.day && calendar.month == selectedDate.month && calendar.year == selectedDate.year
    }

    private fun searchEvent(year : Int, month : Int, day : Int, initCalendar: Boolean = true){
        events.clear()
        val calendar = Calendar()
        calendar.setDate(day, month, year)
        println("CA $calendar")
        val ref = FirebaseDatabase.getInstance().getReference("events/${calendar.year}-${calendar.month+1}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                val listCalendars = ArrayList<com.example.calendar.Calendar>()
                data.forEach {
                    val event = it.getValue(Event::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if(event != null){
                        val date = DateUTC(event.date)
                        if(date.getMonthInt() == calendar.month+1) {
                            println("ICI")
                            val coordsEvent = Location("")
                            coordsEvent.latitude = event.place.lat
                            coordsEvent.longitude = event.place.lng
                            val coordsCurrent = Location("")
                            coordsCurrent.latitude = currentLatLng.latitude
                            coordsCurrent.longitude = currentLatLng.longitude
                            if(coordsEvent.distanceTo(coordsCurrent) < distance) {
                                println("BAH OUI")
                                val c = Calendar()
                                c.setDate(date.getDayInt(), date.getMonthInt()-1, date.getYearInt())
                                listCalendars.add(c)

                                if(isSelectedDay(c))
                                    events.add(EventWithDistance(event, coordsEvent.distanceTo(coordsCurrent) ))
                            }
                        }

                    }
                }
                if(initCalendar)
                    initCalendarWithDate(year, month, day, listCalendars)
                events = ArrayList(events.sortedWith(compareBy{ it.event.date }).reversed())
                val adapter = ArrayAdapterCustom(context, activity, R.layout.my_list, events )
                listView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}

