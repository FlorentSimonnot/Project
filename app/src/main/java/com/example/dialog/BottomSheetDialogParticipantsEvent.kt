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
import com.example.project.PublicUserActivity
import com.example.project.R
import com.example.session.SessionUser
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*

class BottomSheetDialogParticipantsEvent(
    val ctx : Context,
    val resource : Int,
    val keyEvent : String,
    val keyUser : String
) : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var textViewSendMessage : TextView
    private lateinit var textViewDeleteUser : TextView
    private lateinit var textViewSeeProfil : TextView
    private lateinit var layoutSendMessage : RelativeLayout
    private lateinit var layoutDeleteUser : RelativeLayout
    private lateinit var layoutSeeProfil : RelativeLayout
    val sessionUser = SessionUser(ctx)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(resource, container, false)

        textViewSendMessage = v.findViewById(R.id.textViewSendMessage)
        textViewDeleteUser = v.findViewById(R.id.textViewDeleteFriend)
        textViewSeeProfil = v.findViewById(R.id.textViewSeeProfile)
        layoutSendMessage = v.findViewById(R.id.send_message_layout)
        layoutDeleteUser = v.findViewById(R.id.delete_layout)
        layoutSeeProfil = v.findViewById(R.id.see_profile_layout)

        sessionUser.writeInfoUser(ctx, keyUser, textViewSendMessage, "sendMessage")
        sessionUser.writeInfoUser(ctx, keyUser, textViewDeleteUser, "deleteFromEvent")
        sessionUser.writeInfoUser(ctx, keyUser, textViewSeeProfil, "seeProfile")
        layoutDeleteUser.setOnClickListener(this)
        layoutSendMessage.setOnClickListener(this)
        layoutSeeProfil.setOnClickListener(this)


        return v
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.see_profile_layout -> {
                val intent = Intent(ctx, PublicUserActivity::class.java)
                intent.putExtra("user", keyUser)
                startActivity(intent)
            }
            R.id.send_message_layout -> {
                FirebaseDatabase.getInstance().getReference("friends/${sessionUser.getIdFromUser()}/$keyUser").addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.child("keyChat").exists()){
                            val keyChat = p0.child("keyChat").value as String
                            val intent = Intent(ctx, Dialog::class.java)
                            intent.putExtra("keyUser", keyUser)
                            intent.putExtra("keyChat", keyChat)
                            this@BottomSheetDialogParticipantsEvent.dismiss()
                            ctx.startActivity(intent)
                        }
                    }

                })
            }
            R.id.delete_layout -> {
                val deleteFromEvent = AlertDialog.Builder(ctx)
                deleteFromEvent.setTitle("Are you sure?")
                deleteFromEvent.setMessage("Blabalabla :(.\nConfirm?")
                deleteFromEvent.setPositiveButton("Yes"){_, _ ->
                    Event().deleteParticipation(ctx, keyEvent, keyUser)
                    this.dismiss()
                }
                deleteFromEvent.setNegativeButton("No"){_, _ ->

                }
                deleteFromEvent.show()
            }
        }
    }

}