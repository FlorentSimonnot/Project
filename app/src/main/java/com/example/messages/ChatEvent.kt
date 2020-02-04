package com.example.messages

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.*
import com.example.color.ColorWithHexa
import com.example.events.Event
import com.example.project.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class ChatEvent(
    val keyChat : String
) {

    fun closeDiscussion(editText: EditText, buttonSendImage : ImageButton, buttonSendMessage : ImageButton){
        FirebaseDatabase.getInstance().reference
            .child("chatEvent/$keyChat/parameters/status")
            .setValue("close")
            .addOnSuccessListener {
                editText.visibility = View.GONE
                buttonSendImage.visibility = View.GONE
                buttonSendMessage.visibility = View.GONE
            }

    }

    fun activateDiscussion(editText: EditText, buttonSendImage : ImageButton, buttonSendMessage : ImageButton){
        FirebaseDatabase.getInstance().reference
            .child("chatEvent/$keyChat/parameters/status")
            .setValue("open")
    }

    fun modifyNameDiscussion(newName : String){
        FirebaseDatabase.getInstance().reference.child("chatEvent/$keyChat/parameters/name").setValue(newName)
    }

    fun setTitleDiscussion(context: Context, supportActionBar: androidx.appcompat.app.ActionBar){
        FirebaseDatabase.getInstance().getReference("chatEvent/$keyChat/parameters").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("name")){
                    val name = p0.child("name").value as String
                    supportActionBar.title = name
                }
                else{
                    Event().writeInfoTitleDiscussioEvent(context, keyChat, supportActionBar)
                }
            }

        })
    }

    fun setTitleDiscussionOnEditText(context: Context, textView: TextView){
        FirebaseDatabase.getInstance().getReference("chatEvent/$keyChat/parameters").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("name")){
                    val name = p0.child("name").value as String
                    textView.text = name
                }
                else{
                    //Event().writeInfoTitleDiscussioEvent(context, keyChat, editText)
                }
            }

        })
    }


    fun setBackgroundColor(relativeLayout: RelativeLayout, supportActionBar : androidx.appcompat.app.ActionBar, footer : RelativeLayout){
        FirebaseDatabase.getInstance().getReference("chatEvent/$keyChat/parameters").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("color")){
                    val color = p0.child("color").getValue(ColorWithHexa::class.java)
                    relativeLayout.setBackgroundColor(Color.parseColor(color!!.color.getHexa(color.hexa)))
                    supportActionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor(color!!.color.getDarkColor(color.hexa))))
                    footer.setBackgroundColor(Color.parseColor(color!!.color.getDarkColor(color.hexa)))
                }
            }

        })
    }

    fun changeBackgroundColor(colorWithHexa: ColorWithHexa){
        FirebaseDatabase.getInstance().reference
            .child("chatEvent/$keyChat/parameters/color")
            .setValue(colorWithHexa)
    }

    fun isDiscussionClosed(editText: EditText, buttonSendImage : ImageButton, buttonSendMessage : ImageButton, discussionTurnOff : TextView){
        FirebaseDatabase.getInstance().getReference("chatEvent/$keyChat/parameters").addValueEventListener(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.hasChild("status")){
                        if(p0.child("status").value as String == "close"){
                            editText.visibility = View.GONE
                            buttonSendImage.visibility = View.GONE
                            //buttonSendMessage.visibility = View.GONE
                            discussionTurnOff.visibility = View.VISIBLE
                        }
                        else{
                            editText.visibility = View.VISIBLE
                            buttonSendImage.visibility = View.VISIBLE
                            //buttonSendMessage.visibility = View.VISIBLE
                            discussionTurnOff.visibility = View.GONE
                        }
                    }
                }

            }
        )
    }


}