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

class BottomSheetDialogFriend(
    val ctx : Context,
    val resource : Int,
    val keyUser : String
) : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var textViewSendMessage : TextView
    private lateinit var textViewDeleteUser : TextView
    private lateinit var layoutSendMessage : RelativeLayout
    private lateinit var layoutDeleteUser : RelativeLayout
    val sessionUser = SessionUser(ctx)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(resource, container, false)

        textViewSendMessage = v.findViewById(R.id.textViewSendMessage)
        textViewDeleteUser = v.findViewById(R.id.textViewDeleteFriend)
        layoutSendMessage = v.findViewById(R.id.send_message_layout)
        layoutDeleteUser = v.findViewById(R.id.delete_layout)

        sessionUser.writeInfoUser(ctx, keyUser, textViewSendMessage, "sendMessage")
        sessionUser.writeInfoUser(ctx, keyUser, textViewDeleteUser, "deleteFriend")
        layoutDeleteUser.setOnClickListener(this)
        layoutSendMessage.setOnClickListener(this)


        return v
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.send_message_layout -> {
                FirebaseDatabase.getInstance().getReference("friends/${sessionUser.getIdFromUser()}/$keyUser").addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.child("keyChat").exists()){
                            val keyChat = p0.child("keyChat").value as String
                            val intent = Intent(ctx, Dialog::class.java)
                            intent.putExtra("keyUser", keyUser)
                            intent.putExtra("keyChat", keyChat)
                            this@BottomSheetDialogFriend.dismiss()
                            ctx.startActivity(intent)
                        }
                    }

                })
            }
            R.id.delete_layout -> {
                val deleteFriendAlert = AlertDialog.Builder(ctx)
                deleteFriendAlert.setTitle("Are you sure?")
                deleteFriendAlert.setMessage("This user will not be your friend anymore :(.\nConfirm?")
                deleteFriendAlert.setPositiveButton("Yes"){_, _ ->
                    SessionUser(ctx).deleteFriend(ctx, keyUser)
                    this.dismiss()
                }
                deleteFriendAlert.setNegativeButton("No"){_, _ ->

                }
                deleteFriendAlert.show()
            }
        }
    }

}