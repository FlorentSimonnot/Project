package com.example.arrayAdapterCustom

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.example.dateCustom.DateUTC
import com.example.dialog.BottomSheetDialogFriend
import com.example.dialog.BottomSheetDialogParticipantsEvent
import com.example.events.Event
import com.example.place.SessionGooglePlace
import com.example.project.*
import com.example.session.SessionUser
import com.example.user.User
import com.example.user.UserWithKey
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import java.util.*
import kotlin.collections.ArrayList


class ArrayAdapterEventOnMap(
    private val ctx : Context,
    private val resource : Int,
    private val events : ArrayList<Event>
): ArrayAdapter<Event>( ctx , resource, events){
    private lateinit var logoSport : ImageView
    private lateinit var nameEvent : TextView
    private lateinit var day : TextView
    private lateinit var month : TextView
    private lateinit var participantsNumber : TextView


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val v = layoutInflater.inflate(resource, null)

        logoSport = v.findViewById(R.id.icon_sport)
        nameEvent = v.findViewById(R.id.event_name)
        participantsNumber = v.findViewById(R.id.event_participants_number)
        day = v.findViewById(R.id.day)
        month = v.findViewById(R.id.month)

        Event().writeLogoSport(ctx, events[position].key, logoSport, 50)
        nameEvent.text = events[position].name
        day.text = DateUTC(events[position].date).getDay()
        month.text = DateUTC(events[position].date).getMonthLetter()
        Event().writeInfoEvent(ctx, events[position].key, participantsNumber, "freePlace")

        return v
    }

}