package com.example.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.events.Event
import com.example.project.Dialog
import com.example.project.EventInfoViewParticipantActivity
import com.example.project.R
import com.example.session.SessionUser
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*

class BottomSheetDialogParticipantsWaitingEvent(
    val ctx : Context,
    val resource : Int,
    val keyEvent : String,
    val keyUser : String
) : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var textViewAccept : TextView
    private lateinit var textViewRefuse : TextView
    private lateinit var layoutAccept : RelativeLayout
    private lateinit var layoutRefuse : RelativeLayout
    val sessionUser = SessionUser(ctx)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(resource, container, false)

        textViewAccept = v.findViewById(R.id.textViewAccept)
        textViewRefuse = v.findViewById(R.id.textViewRefuse)
        layoutAccept = v.findViewById(R.id.accept_layout)
        layoutRefuse = v.findViewById(R.id.refuse_layout)

        sessionUser.writeInfoUser(ctx, keyUser, textViewAccept, "AcceptJoinEvent")
        sessionUser.writeInfoUser(ctx, keyUser, textViewRefuse, "RefuseJoinEvent")
        layoutRefuse.setOnClickListener(this)
        layoutAccept.setOnClickListener(this)


        return v
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.accept_layout -> {
                this.dismiss()
                Event().confirmParticipation(ctx, keyEvent, keyUser)
            }
            R.id.refuse_layout -> {
                this.dismiss()
                Event().refuseParticipation(ctx, keyEvent, keyUser!!)
            }
        }
    }

}