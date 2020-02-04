package com.example.arrayAdapterCustom

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import com.example.dateCustom.DateUTC
import com.example.events.Distance
import com.example.events.Event
import com.example.events.EventWithDistance
import com.example.images.Image
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


class ArrayAdapterCustom(private val ctx : Context , val activity : Activity, private val resource : Int, private val events : ArrayList<EventWithDistance>)
    : ArrayAdapter<EventWithDistance>( ctx , resource, events){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater :LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )

        val imageView :ImageView = view.findViewById(R.id.icon_sport)
        val textView : TextView = view.findViewById(R.id.event_name)
        val textView1 : TextView = view.findViewById(R.id.event_desc)
        val time = view.findViewById<TextView>(R.id.time)
        val month = view.findViewById<TextView>(R.id.month)
        val distance = view.findViewById<TextView>(R.id.event_distance)
        val numberParticipants : TextView = view.findViewById(R.id.event_participants)

        imageView.setImageDrawable(ctx.resources.getDrawable(events[position].event.sport.getLogoSport()))
        textView.text = events[position].event.name.toString().toUpperCase()
        time.text = "${DateUTC(events[position].event.date).showHour()}H${DateUTC(events[position].event.date).showMinutes()}"
        Event().writeInfoEvent(ctx, events[position].event.key, numberParticipants, "numberOfParticipants")
        view.setOnClickListener {
            if(events[position].event.creator != SessionUser(context).getIdFromUser()){
                context.startActivity(
                    Intent(context, EventInfoViewParticipantActivity::class.java)
                        .addCategory("eventInfo")
                        .putExtra("key", events[position].event.key)
                )
            }
            else{
                context.startActivity(
                    Intent(context, EventInfoJojoActivity::class.java)
                        .addCategory("eventInfo")
                        .putExtra("key", events[position].event.key)
                )
            }
        }

        textView1.text = events[position].event.place.address
        val distanceV = Distance(events[position].distance.toDouble())
        distanceV.convertMetersToKilometers()
        SessionUser(ctx).writeKmOrMiles(distanceV, distance, false)

        return view
    }

}