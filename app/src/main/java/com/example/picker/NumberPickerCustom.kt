package com.example.picker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment

class NumberPickerCustom(
    private val min : Int,
    private val max : Int,
    private val title : String,
    private val message : String
) : DialogFragment()
{
    lateinit var valueChange : NumberPicker.OnValueChangeListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numberPicker = NumberPicker(activity)
        numberPicker.maxValue = max
        numberPicker.minValue = min

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)

        //P0 = DialogInterface / P1 = Int
        builder.setPositiveButton("OK") { p0, p1 ->
            valueChange.onValueChange(numberPicker, numberPicker.value, numberPicker.value)
        }

        builder.setNegativeButton("CANCEL") { p0, p1 ->
            //valueChange.onValueChange(numberPicker, numberPicker.value, numberPicker.value)
        };

        builder.setView(numberPicker);
        return builder.create();
    }

    fun getValueChangeListener(): NumberPicker.OnValueChangeListener {
        return valueChange
    }

    fun setValueChangeListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        valueChange = valueChangeListener
    }

}
