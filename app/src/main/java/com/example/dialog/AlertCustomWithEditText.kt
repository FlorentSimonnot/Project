package com.example.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.project.R


class AlertCustomWithEditText(
    val ctx : Context,
    val resource : Int,
    val textViewResult : TextView,
    val title : String,
    val message : String,
    val keyEvent: String,
    val action : String
) : AppCompatDialogFragment() {
    private lateinit var edit : EditText
    private var listener: ExampleDialogListener? = null

    interface ExampleDialogListener {
        fun applyText(title : String, textView : TextView)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(resource, null)

        builder.setView(view)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton("Cancel"){ _, _ ->

            }
            .setPositiveButton("Ok"){ _, _ ->
                //textViewResult.text = edit.text
                listener!!.applyText(edit.text.toString(), textViewResult)
                //Toast.makeText(ctx, "${edit.text}", Toast.LENGTH_SHORT).show()
            }

        edit = view.findViewById(R.id.edit_field)
        edit.setText(textViewResult.text.toString())


        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as ExampleDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must implement ExampleDialogListener")
        }

    }

}