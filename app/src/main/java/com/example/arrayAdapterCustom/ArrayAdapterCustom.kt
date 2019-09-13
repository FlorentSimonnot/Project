package com.example.arrayAdapterCustom

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import com.example.dateCustom.DateUTC
import com.example.events.Event
import com.example.place.SessionGooglePlace
import com.example.project.EventInfoActivity
import com.example.project.EventInfoJojoActivity
import com.example.project.EventInfoViewParticipantActivity
import com.example.project.R
import com.example.session.SessionUser
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import java.util.*
import kotlin.collections.ArrayList


class ArrayAdapterCustom(private val ctx : Context , private val resource : Int, private val events : ArrayList<Event>)
    : ArrayAdapter<Event>( ctx , resource, events){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater :LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView :ImageView = view.findViewById(R.id.icon_sport)
        val textView : TextView = view.findViewById(R.id.event_name)
        val textView1 : TextView = view.findViewById(R.id.event_desc)
        val day = view.findViewById<TextView>(R.id.day)
        val month = view.findViewById<TextView>(R.id.month)


        imageView.setImageDrawable(ctx.resources.getDrawable(events[position].sport.getLogoSport()))
        textView.text = events[position].name
        day.text = DateUTC(events[position].date).getDay()
        month.text = DateUTC(events[position].date).getMonthLetter()
        view.setOnClickListener {
            if(events[position].creator != SessionUser(context).getIdFromUser()){
                context.startActivity(
                    Intent(context, EventInfoViewParticipantActivity::class.java)
                        .addCategory("eventInfo")
                        .putExtra("key", events[position].key)
                )
            }
            else{
                context.startActivity(
                    Intent(context, EventInfoJojoActivity::class.java)
                        .addCategory("eventInfo")
                        .putExtra("key", events[position].key)
                )
            }
        }

        textView1.text = events[position].place.address

        /*//Init google place
        val gg = SessionGooglePlace(ctx)
        gg.init()
        val placesClient = gg.createClient()

        //Search place in according to the ID
        val placeId : String = events[position].place!!
        val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME)
        val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)


        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                val place : Place = it.place
                textView1.text = place.address
            }
            .addOnFailureListener {
                textView1.text = it.message
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }*/


        return view
    }

}