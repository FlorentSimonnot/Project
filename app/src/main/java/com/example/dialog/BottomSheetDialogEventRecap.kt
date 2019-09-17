package com.example.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.example.arrayAdapterCustom.ArrayAdapterEventOnMap
import com.example.events.Event
import com.example.project.EventInfoViewParticipantActivity
import com.example.project.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*

class BottomSheetDialogEventRecap(
    val ctx : Context,
    val resource : Int,
    val events : ArrayList<Event>
) : BottomSheetDialogFragment(){
    private lateinit var adapter : ArrayAdapterEventOnMap
    private lateinit var numberEvent : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(resource, container, false)
        val listView = v.findViewById<ListView>(R.id.listView)
        numberEvent = v.findViewById(R.id.numberEvent)
        numberEvent.text = "There are ${events.size} events here"
        adapter = ArrayAdapterEventOnMap(
            ctx,
            R.layout.list_event_map_bottom,
            events
        )
        listView.adapter = adapter
        listView.setOnItemClickListener { adapterView, view, i, l ->
            ctx.startActivity(Intent(ctx, EventInfoViewParticipantActivity::class.java).putExtra("key", events[i].key)            )
        }
        return v
    }

}