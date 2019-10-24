package com.example.picker

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.project.R

class StringPickerCustom(
    private val ctx : Context,
    private val min : Int,
    private val max : Int,
    private val title : String,
    private val message : String,
    private val values : Array<String>,
    private val value : String = ""
) : DialogFragment()
{
    lateinit var valueChange : NumberPicker.OnValueChangeListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val numberPicker = NumberPicker(ctx)
        numberPicker.maxValue = max
        numberPicker.minValue = min
        numberPicker.displayedValues = values

        numberPicker.displayedValues.forEachIndexed { index, _ ->
            val child = numberPicker.getChildAt(index)
            if(child != null) {
                val editText = child!! as EditText
                editText.setTextColor(ctx.resources.getColor(R.color.colorText))
            }
        }

        if(value.isNotEmpty()){
            numberPicker.value = values.indexOf(value)
        }

        val builder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.MyDialogTheme))
        builder.setTitle(title)
        builder.setMessage(message)

        //P0 = DialogInterface / P1 = Int
        builder.setPositiveButton(getString(R.string.ok)) { p0, p1 ->
            valueChange.onValueChange(numberPicker, numberPicker.value, numberPicker.value)
        }

        builder.setNegativeButton(getString(R.string.cancel)) { p0, p1 ->
            //valueChange.onValueChange(numberPicker, numberPicker.value, numberPicker.value)
        }

        builder.setView(numberPicker)
        return builder.create()
    }

    fun getValueChangeListener(): NumberPicker.OnValueChangeListener {
        return valueChange
    }

    fun setValueChangeListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        valueChange = valueChangeListener
    }
}