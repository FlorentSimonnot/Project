package com.example.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory



class Image(val context : Context, val resource : Int) {

    fun getBitmap() : Bitmap{
       return BitmapFactory.decodeResource(context.resources, resource)
    }

    fun resizeImage(newWidth : Int) : Bitmap{
        println("NEW $newWidth")
        if(newWidth > getBitmap().width){
            val ratio = newWidth/getBitmap().width * 1.0f
            return Bitmap.createScaledBitmap(getBitmap(), newWidth, (getBitmap().height * ratio).toInt() , true)
        }
        else{
            val ratio = getBitmap().width/newWidth * 1.0f
            return Bitmap.createScaledBitmap(getBitmap(), newWidth, (getBitmap().height / ratio).toInt()-20 , true)
        }

    }

}