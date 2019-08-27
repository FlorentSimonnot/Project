package com.example.utils

import android.widget.ImageView
import android.widget.TextView
import com.example.dateCustom.DateCustom
import com.example.dateCustom.TimeCustom
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.Serializable

data class Image(
    val urlPhoto : String,
    val date : String,
    val time : String
) : Serializable {

    fun showImage(imageView: ImageView){
        val ref = FirebaseStorage.getInstance().getReference(urlPhoto).downloadUrl
        ref.addOnSuccessListener {
            Picasso.get().load(it).into(imageView)
        }
    }

    fun showDate(textView: TextView){
        textView.text = DateCustom(date).toString()
    }

    fun showTime(textView: TextView){
        textView.text = TimeCustom(time).showTime()
    }

}