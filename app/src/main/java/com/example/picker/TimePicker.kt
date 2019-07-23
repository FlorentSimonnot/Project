package com.example.picker

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.app.TimePickerDialog
import android.text.format.DateFormat
import java.util.*


class TimePicker : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(
            activity, activity as TimePickerDialog.OnTimeSetListener?, hour, minute, DateFormat.is24HourFormat(
                activity
            )
        )
    }
}