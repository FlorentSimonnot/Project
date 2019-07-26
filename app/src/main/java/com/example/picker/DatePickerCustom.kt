package com.example.picker

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import com.example.project.R
import android.view.Gravity
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.*


/**DatePickerCustom create a DatePicker width numberPicker for the day, month and year
 *
 */
class DatePickerCustom(
    private val context : Context,
    private val textViewResult : TextView
) : NumberPicker.OnValueChangeListener {

    private val daysPicker : NumberPicker = NumberPicker(context)
    private val monthsPicker : NumberPicker = NumberPicker(context)
    private val yearsPicker : NumberPicker = NumberPicker(context)

    private val monthWith31days : IntArray = intArrayOf(1, 3, 5, 7, 8, 10, 12)

    private lateinit var alertDialog : AlertDialog

    private var dayChoosen : Int = 1
    private var monthChoosen : Int = 1
    private var yearChoosen : Int = 2000

    /**
     * initDatePicker create the pickers and theirs position in a dialog
     *
     * @author Florent Simonnot
     */
    fun initDatePicker(){
        //The first linearLayout which contain all element
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL

        val params = LinearLayout.LayoutParams(50, 50)
        params.gravity = Gravity.CENTER

        val dayParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        dayParams.weight = 1F
        dayParams.setMargins(10, 10, 10, 10)
        val monthParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        monthParams.weight = 1F
        monthParams.setMargins(10, 10, 10, 10)
        val yearParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        yearParams.weight = 1F
        yearParams.setMargins(10, 10, 10, 10)

        linearLayout.layoutParams = params
        linearLayout.addView(daysPicker, dayParams)
        linearLayout.addView(monthsPicker, monthParams)
        linearLayout.addView(yearsPicker, yearParams)

        //Set values for pickers
        setDays(1, 31)
        setMonth()
        setYear(1920, 2003)
        //Set values changes for pickers
        daysPicker.setOnValueChangedListener { numberPicker, old, new ->
            dayChoosen = numberPicker.value.toInt()
        }
        monthsPicker.setOnValueChangedListener { numberPicker, old, new ->
            monthChoosen = numberPicker.value.toInt()+1
            updateValues(Action.MONTH)
        }
        yearsPicker.setOnValueChangedListener { numberPicker, old, new ->
            yearChoosen = numberPicker.value.toInt()
            updateValues(Action.YEAR)
        }

        //Create alert dialog
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Select your birthday")
        alertDialogBuilder.setView(linearLayout)

        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("OK") { p0, p1 ->
                writeDate()
                //valueChange.onValueChange(numberPicker, numberPicker.value, numberPicker.value)
            }
            .setNegativeButton("CANCEL") { p0, p1 ->
                //valueChange.onValueChange(numberPicker, numberPicker.value, numberPicker.value)
            }
        alertDialog = alertDialogBuilder.create()
    }

    enum class Action{
        DAY,
        MONTH,
        YEAR,
        ERROR
    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        if(p0 != null){
            //Know which numberPicker is touch
            when(p0.maxValue){
                11 -> {
                    monthChoosen = p0.value.toInt()
                    updateValues(Action.MONTH)
                }
                28, 29, 30, 31 ->{
                    dayChoosen = p0.value.toInt()
                    updateValues(Action.DAY)
                }
                2003 ->{
                    yearChoosen = p0.value.toInt()
                    updateValues(Action.YEAR)
                }
                else -> Action.ERROR
            }
        }
    }

    /**
     * updateValues update the day in accordance with the month or year.
     * For example, if action is month and February is selected we change the max day. We check also the year.
     */
    private fun updateValues(action : Action){
        when(action){
            Action.DAY -> {
                //Nothing
            }
            Action.MONTH -> {
                if(monthWith31days.contains(monthChoosen)){
                    setDays(1, 31)
                }
                else{
                    //Not February
                    if(monthChoosen != 2){
                        setDays(1, 30)
                    }
                    else{
                        //Leap year = année bisextile
                        if(isLeapYear(yearChoosen)){
                            setDays(1, 29)
                        }
                        else {
                            setDays(1, 28)
                        }
                    }
                }
            }
            Action.YEAR -> {
                if(monthChoosen == 2) {
                    if(isLeapYear(yearChoosen))
                        setDays(1, 29)
                    else{
                        setDays(1, 28)
                    }
                }
            }
            else -> throw Exception("BAD ACTION")
        }
    }

    fun isLeapYear(year : Int) : Boolean{
        if(year%4 == 0){
            if(year%100 != 0){
                return true
            }
        }
        if(year%400 == 0){
            return true
        }
        return false
    }

    fun showDialog(){
        alertDialog.show()
    }

    private fun setMonth(){
        monthsPicker.minValue = 0
        monthsPicker.maxValue = 11
        monthsPicker.displayedValues = context.resources.getStringArray(R.array.month_en)
    }

    private fun setDays(min : Int, max : Int){
        daysPicker.minValue = min
        daysPicker.maxValue = max
    }

    private fun setYear(min : Int, max : Int){
        yearsPicker.minValue = min
        yearsPicker.maxValue = max
        yearsPicker.value = 2000
    }

    /**
     * setDesign set the text color and the divider color for all numberPickers
     * @param color : the color we want to set
     * @author Florent Simonnot
     */
    fun setDesign(color : Int){
        /*
         *****
         * Colorize the text for all numberPicker
         *****
         */
        setNumberPickerTextColor(daysPicker, color)
        setNumberPickerTextColor(monthsPicker, color)
        setNumberPickerTextColor(yearsPicker, color)
        /*
         *****
         * Colorize the divider for all numberPicker
         *****
         */
        setDividerColor(daysPicker, color)
        setDividerColor(monthsPicker, color)
        setDividerColor(yearsPicker, color)
    }

    private fun writeDate(){
        var zeroDay = ""
        var zeroMonth = ""
        if(dayChoosen < 10){
            zeroDay = "0"
        }
        if(monthChoosen < 10){
            zeroMonth = "0"
        }
        textViewResult.text = "${zeroDay}${dayChoosen.toString()}/${zeroMonth}${monthChoosen.toString()}/${yearChoosen.toString()}"
    }

    private fun setNumberPickerTextColor(numberPicker: NumberPicker, color: Int) {

        try {
            val selectorWheelPaintField = numberPicker.javaClass
                .getDeclaredField("mSelectorWheelPaint")
            selectorWheelPaintField.setAccessible(true)
            (selectorWheelPaintField.get(numberPicker) as Paint).setColor(color)
        } catch (e: NoSuchFieldException) {
            //Error
        } catch (e: IllegalAccessException) {
            //Error
        } catch (e: IllegalArgumentException) {
            //Error
        }

        val count = numberPicker.childCount
        for (i in 0 until count) {
            val child = numberPicker.getChildAt(i)
            if (child is EditText)
                child.setTextColor(color)
        }
        numberPicker.invalidate()
    }

    private fun setDividerColor(picker: NumberPicker, color: Int) {

        val pickerFields = NumberPicker::class.java!!.getDeclaredFields()
        for (pf in pickerFields) {
            if (pf.getName() == "mSelectionDivider") {
                pf.setAccessible(true)
                try {
                    val colorDrawable = ColorDrawable(color)
                    pf.set(picker, colorDrawable)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

                break
            }
        }
    }
}