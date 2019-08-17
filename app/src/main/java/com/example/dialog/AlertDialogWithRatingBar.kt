package com.example.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import com.example.project.EventActivity
import com.example.project.R

class AlertDialogWithRatingBar(
    var context: Context,
    var title : String
) {
    private lateinit var alertDialog: AlertDialog

    fun create(){
        // Create a alert dialog builder.
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setNegativeButton("cancel"){p0, p1 ->
            //Nothing
        }

        val inflater = LayoutInflater.from(context)
        var customView = inflater.inflate(R.layout.button_see_events_rating, null);
        builder.setView(customView)
        builder.setCancelable(true)

       alertDialog = builder.create()

        val button = customView.findViewById<Button>(R.id.ratingButton)
        button.setOnClickListener {
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra("joinedView", "true")
            context.startActivity(intent)
        }
    }

    fun show(){
        alertDialog.show()
    }
}