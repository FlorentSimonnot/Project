package com.example.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.arrayAdapterCustom.ArrayAdapterInvitations
import com.example.user.User

class AlertDialogCustom(
    val ctx : Context,
    val resource : Int,
    val users : ArrayList<User>
)  {
    private val alertDialog : AlertDialog.Builder = AlertDialog.Builder(ctx)
    private val adapter = ArrayAdapterInvitations(ctx, resource, users)

    fun createAlertDialog(){
        alertDialog.setTitle("Users waiting")
        alertDialog.setNegativeButton("Cancel"){p0, p1 ->
            //Nothing
        }
        alertDialog.setAdapter(adapter){p0, p1 ->
            //Nothing
        }

    }

    fun showDialog(){
        alertDialog.show()
    }
}