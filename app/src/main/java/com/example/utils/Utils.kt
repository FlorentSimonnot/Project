package com.example.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Utils {
    private val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    fun generatePassword(size : Int) : String{
        return (1..size)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.length) }
            .map(charPool::get)
            .joinToString("");
    }

    fun insertImage(urlPhoto : String, uri : Uri){
        val refPhotoStorage = FirebaseStorage.getInstance().getReference("/images")
        refPhotoStorage.child(urlPhoto).putFile(uri)
    }
}