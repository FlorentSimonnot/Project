package com.example.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.arrayAdapterCustom.ArrayAdapterInvitations
import com.example.user.User
import com.example.user.UserWithKey

class AlertDialogCustom(
    val ctx : Context,
    val resource : Int,
    val users : ArrayList<UserWithKey>,
    val title : String,
    val keyEvent: String,
    val action : String
) : AlertDialog(ctx) {
    private val alertDialog : AlertDialog.Builder = AlertDialog.Builder(ctx)
    private val adapter = ArrayAdapterInvitations(ctx, resource, users, keyEvent, action)

    fun createAlertDialog(){
        alertDialog.setTitle(title)
        alertDialog.setNegativeButton("Cancel"){p0, p1 ->
            //Nothing
        }
        adapter.notifyDataSetChanged()
        alertDialog.setAdapter(adapter){p0, p1 ->
            //Nothing
        }

    }

    fun showDialog(){
        alertDialog.show()
    }
}