package com.example.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.project.R


class DialogProfilView(
    val ctx : Context,
    val resource : Int,
    val userKey : String
) : AppCompatDialogFragment() {
    private lateinit var identity : TextView



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(resource, null)

        builder.setView(view)
            .setNegativeButton("Cancel"){ _, _ ->

            }
            .setPositiveButton("Ok"){ _, _ ->
            }


        return builder.create()
    }

    fun show(){
        this.show()
    }


}