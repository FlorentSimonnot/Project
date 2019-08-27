package com.example.utils

import android.net.Uri
import android.widget.ImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class Utils {
    private val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    fun generatePassword(size : Int) : String{
        return (1..size)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.length) }
            .map(charPool::get)
            .joinToString("");
    }

    fun insertImage(urlPhoto : String, uri : Uri, sender : String, addressee : String){
        val refKeyChat = FirebaseDatabase.getInstance().getReference("friends/$sender/$addressee")
        refKeyChat.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.child("keyChat").value
                val refPhotoStorage = FirebaseStorage.getInstance().getReference("/images/discussions/$value")
                refPhotoStorage.child(urlPhoto).putFile(uri)
            }

        })
    }

}