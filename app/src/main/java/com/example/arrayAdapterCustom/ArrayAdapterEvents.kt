package com.example.arrayAdapterCustom

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.dateCustom.DateUTC
import com.example.events.Event
import com.example.place.SessionGooglePlace
import com.example.project.*
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList


class ArrayAdapterEvents(
    private val ctx : Context,
    private val resource : Int,
    private val events : ArrayList<Event>,
    private val action : String
): ArrayAdapter<Event>( ctx , resource, events){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view : View = layoutInflater.inflate(resource , null )
        val textView : TextView = view.findViewById(R.id.event_name)
        val day = view.findViewById<TextView>(R.id.day)
        val month = view.findViewById<TextView>(R.id.month)
        val textViewDescription : TextView = view.findViewById(R.id.event_desc)
        val iconSport : ImageView = view.findViewById(R.id.icon_sport)

        if(events.size > 0) {
            textView.text = events[position].name
            Event().writeInfoEvent(ctx, events[position].key, textViewDescription, "place")
            textViewDescription.text = events[position].place.address
            day.text = DateUTC(events[position].date).getDay()
            month.text = DateUTC(events[position].date).getMonthLetter()
            val sport : Sport = events[position].sport
            iconSport.setImageResource(sport.getLogoSport())
            view.setOnClickListener {
                if(events[position].creator != SessionUser(context).getIdFromUser()){
                        val intent = Intent(context, EventInfoViewParticipantActivity::class.java)
                        intent.addCategory("eventInfo")
                        intent.putExtra("key", events[position].key)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                }
                else{
                    val intent = Intent(context, EventInfoJojoActivity::class.java)
                    intent.addCategory("eventInfo")
                    intent.putExtra("key", events[position].key)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            }
        }

        return view
    }

}