package com.example.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.messages.ChatEvent
import com.example.project.R


class AlertCustomTitle(
    val ctx : Context,
    val resource : Int,
    val title : String,
    val message : String,
    val keyEvent: String,
    val action : String
) : AppCompatDialogFragment() {
    private lateinit var edit : EditText

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
                ChatEvent(keyEvent).modifyNameDiscussion(edit.text.toString())
            }

        edit = view.findViewById(R.id.edit_field)
        ChatEvent(keyEvent).setTitleDiscussionOnEditText(ctx, edit)


        return builder.create()
    }


}