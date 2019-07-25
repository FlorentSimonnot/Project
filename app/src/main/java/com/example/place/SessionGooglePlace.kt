package com.example.place

import android.content.Context
import android.widget.Toast
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class SessionGooglePlace(
    private val context : Context
) {
    private val API_KEY = "AIzaSyDdY6X8SWrQv4o8bR2dM_c8AX7C2-4n434"

    fun init(){
        Places.initialize(context, API_KEY)
    }

    fun createClient() : PlacesClient{
        return Places.createClient(context)
    }

}