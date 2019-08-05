package com.example.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.arrayAdapterCustom.ArrayAdapterInvitations
import com.example.arrayAdapterCustom.ArrayAdapterSport
import com.example.sport.Sport
import com.example.user.User
import com.example.user.UserWithKey
import kotlinx.android.synthetic.main.activity_create_event.*
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_forgot_password.*


class AlertDialogCustom(
    val ctx : Context,
    val resource : Int,
    val sports : ArrayList<Sport>,
    val title : String,
    val textView: TextView
) : AlertDialog(ctx) {
    private val alertDialogBuilder : AlertDialog.Builder = AlertDialog.Builder(ctx)
    private lateinit var alertDialog : AlertDialog
    private val adapter = ArrayAdapterSport(ctx, resource, sports)

    fun createAlertDialog(){
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setNegativeButton("Cancel"){p0, p1 ->
            //Nothing
        }
        adapter.notifyDataSetChanged()
        alertDialogBuilder.setAdapter(adapter, DialogInterface.OnClickListener {
                _, i ->
            val sport = adapter.getItem(i)
            textView.text = sport?.name
            textView.setCompoundDrawablesWithIntrinsicBounds(sport!!.getLogo(), 0, 0, 0)
            Toast.makeText(ctx, "${adapter.getItem(i)}", Toast.LENGTH_SHORT).show()
        })
        alertDialog = alertDialogBuilder.create()
    }

    fun showDialog(){
        alertDialog.show()
        val width = (ctx.resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (ctx.resources.displayMetrics.heightPixels * 0.50).toInt()

        alertDialog.window?.setLayout(width, height)
    }
}