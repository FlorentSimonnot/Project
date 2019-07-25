package com.example.arrayAdapterCustom

import android.content.ContentValues.TAG
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.graphics.ColorSpace
import android.util.Log
import android.view.View
import android.widget.*
import com.example.events.Event
import com.example.place.SessionGooglePlace
import com.example.project.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*
import kotlin.collections.ArrayList


class ArrayAdapterCustom(private val ctx : Context , private val resource : Int, private val events : ArrayList<Event>)
    : ArrayAdapter<Event>( ctx , resource, events){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater :LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView :ImageView = view.findViewById(R.id.icon_sport)
        var textView : TextView = view.findViewById(R.id.event_name)
        var textView1 : TextView = view.findViewById(R.id.event_desc)


        imageView.setImageDrawable(ctx.resources.getDrawable(events[position].sport.getLogo()))
        textView.text = events[position].name

        //Init google place
        val gg = SessionGooglePlace(ctx)
        gg.init()
        val placesClient = gg.createClient()

        //Search place in according to the ID
        val placeId : String = events[position].place!!
        val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME)
        val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)


        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                val place : Place = it.place
                textView1.text = place.name
            }
            .addOnFailureListener {
                textView1.text = it.message
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }


        return view
    }

}