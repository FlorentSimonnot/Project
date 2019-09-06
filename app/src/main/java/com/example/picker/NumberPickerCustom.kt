package com.example.picker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.project.R

class NumberPickerCustom(
    private val min : Int,
    private val max : Int,
    private val title : String,
    private val message : String,
    private val value : Int = 0
) : DialogFragment()
{
    lateinit var valueChange : NumberPicker.OnValueChangeListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numberPicker = NumberPicker(activity)
        numberPicker.maxValue = max
        numberPicker.minValue = min

        if(value > 0){
            numberPicker.value = value
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)

        //P0 = DialogInterface / P1 = Int
        builder.setPositiveButton(getString(R.string.ok)) { p0, p1 ->
            valueChange.onValueChange(numberPicker, numberPicker.value, numberPicker.value)
        }

        builder.setNegativeButton(getString(R.string.cancel)) { p0, p1 ->
            //valueChange.onValueChange(numberPicker, numberPicker.value, numberPicker.value)
        }

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
