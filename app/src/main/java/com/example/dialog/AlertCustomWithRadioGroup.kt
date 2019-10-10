package com.example.dialog

import android.content.Context
import android.widget.RadioButton
import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.example.project.R
import android.widget.Toast
import android.content.DialogInterface
import android.widget.ArrayAdapter
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList


class AlertCustomWithRadioGroup(
    val context: Context,
    val activity : Activity,
    val values : ArrayAdapter<String>,
    val title : String
) {
    private val builder : AlertDialog.Builder = AlertDialog.Builder(context)
    private lateinit var dialog : AlertDialog

    fun create(bool : Boolean) {

        val session = SessionUser(context)

        builder.setTitle(title)
        builder.setPositiveButton("Ok") {
                dialog, id -> session.getNightMode()
        }
        builder.setNegativeButton("Cancel") {
                dialog, id -> session.setNightMode(bool)
        }
        val int = if(bool){0}else{1}
        builder.setSingleChoiceItems(values, int, DialogInterface.OnClickListener { dialog, item ->
            when(item){
                0 -> {
                    session.setNightMode(true)
                }
                1 -> {
                    session.setNightMode(false)
                }
            }
        }
        )

        dialog = builder.create()
        dialog.show()
    }

}