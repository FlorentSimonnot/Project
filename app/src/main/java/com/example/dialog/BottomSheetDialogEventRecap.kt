package com.example.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.events.Event
import com.example.project.EventInfoViewParticipantActivity
import com.example.project.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*

class BottomSheetDialogEventRecap(
    val ctx : Context,
    val resource : Int,
    val keyEvent : String
) : BottomSheetDialogFragment() {
    private lateinit var logoSport : ImageView
    private lateinit var nameEvent : TextView
    private lateinit var dateEvent : TextView
    private lateinit var buttonMoreInfo :  TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(resource, container, false)

        logoSport = v.findViewById(R.id.sport_logo)
        nameEvent = v.findViewById(R.id.name_event)
        dateEvent = v.findViewById(R.id.date_event)
        buttonMoreInfo = v.findViewById(R.id.more_info_event)

        Event().writeLogoSport(ctx, keyEvent, logoSport, 50)
        Event().writeInfoEvent(ctx, keyEvent, nameEvent, "name")
        Event().writeInfoEvent(ctx, keyEvent, dateEvent, "date")

        buttonMoreInfo.setOnClickListener {
            val intent = Intent(ctx, EventInfoViewParticipantActivity::class.java)
            intent.putExtra("key", keyEvent)
            ctx.startActivity(intent)
        }

        return v
    }
}