package com.example.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.project.R
import com.example.session.SessionUser
import com.example.user.User
import de.hdodenhof.circleimageview.CircleImageView


class DialogProfilView(
    val ctx : Context,
    val resource : Int,
    val userKey : String
) : AlertDialog(ctx) {
    private val alertDialogBuilder : AlertDialog.Builder = AlertDialog.Builder(ctx)
    private lateinit var alertDialog : AlertDialog
    private lateinit var identity : TextView
    private lateinit var gender : TextView
    private lateinit var birthday : TextView
    private lateinit var city : TextView
    private lateinit var button : ImageButton
    private lateinit var photo : CircleImageView


    fun createDialog() {
        val inflater = layoutInflater
        val view = inflater.inflate(resource, null)

        identity = view.findViewById(R.id.identity)
        gender = view.findViewById(R.id.sex_account)
        birthday = view.findViewById(R.id.birthday_account)
        city = view.findViewById(R.id.city_account)
        button = view.findViewById(R.id.close)
        photo = view.findViewById(R.id.profile_photo)

        SessionUser(ctx).writeInfoUser(ctx, userKey, identity, "identity")
        SessionUser(ctx).writeInfoUser(ctx, userKey, gender, "sex")
        SessionUser(ctx).writeInfoUser(ctx, userKey, birthday, "birthday")
        SessionUser(ctx).writeInfoUser(ctx, userKey, city, "city")
        User().showPhotoUser(ctx, photo, userKey)

        button.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog = alertDialogBuilder.create()
        alertDialog.setView(view)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }

    fun showDialog(){
        alertDialog.show()
        val width = (ctx.resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (ctx.resources.displayMetrics.heightPixels * 0.70).toInt()
        alertDialog.window?.setLayout(width, height)
    }


}