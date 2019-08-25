package com.example.messages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.events.Privacy
import com.example.project.PrivateUserActivity
import com.example.project.PublicUserActivity
import com.example.session.SessionUser
import com.example.user.PrivacyAccount
import com.example.user.User
import com.example.user.UserWithKey
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.list_item_message.view.*
import kotlinx.android.synthetic.main.search_list_item_user.view.*
import kotlinx.android.synthetic.main.search_list_item_user.view.profile_photo


class MessageAdapter(
    val context: Context,
    val resourceMessageMe : Int,
    val resourceMessageOther : Int,
    val messages : ArrayList<Message>
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType){
            0 -> {
                val layoutInflater : LayoutInflater = LayoutInflater.from(context)
                val view : View = layoutInflater.inflate(resourceMessageOther , null )
                AddresseeMessage(view)
            }
            1 -> {
                val layoutInflater : LayoutInflater = LayoutInflater.from(context)
                val view : View = layoutInflater.inflate(resourceMessageMe , null )
                SenderMessage(view)
            }
            else -> {
                throw Exception("NOPE")
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        if(message.sender == SessionUser().getIdFromUser()){
            return 1
        }
        return 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    open class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        open fun bind(message : Message) {}
    }

    inner class SenderMessage(view : View) : ViewHolder(view){

        override fun bind(message: Message) {
            text.text = message.text
            time.text = message.time
            text.setOnClickListener {
                if(time.visibility == View.GONE){
                    time.visibility = View.VISIBLE
                }
                else{
                    time.visibility = View.GONE
                }
            }
            text.setOnLongClickListener {
                message.deleteMessage(context)
                true
            }
        }

        val text = view.message
        val time = view.time
    }

    inner class AddresseeMessage(view : View) : ViewHolder(view){

        override fun bind(message: Message) {
            text.text = message.text
            time.text = message.time
            User().showPhotoUser(context, photo, message.sender)
            text.setOnClickListener {
                if(time.visibility == View.GONE){
                    time.visibility = View.VISIBLE
                }
                else{
                    time.visibility = View.GONE
                }
            }
            photo.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().getReference("users/${message.sender}")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //Nothing
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val user = p0.getValue(User::class.java)
                        if(user != null){
                            if(user.privacyAccount == PrivacyAccount.Public){
                                val intent = Intent(context, PublicUserActivity::class.java)
                                intent.putExtra("user", message.sender)
                                context.startActivity(intent)
                            }
                            else{
                                val intent = Intent(context, PrivateUserActivity::class.java)
                                intent.putExtra("user", message.sender)
                                context.startActivity(intent)
                            }
                        }
                    }

                })
            }
        }

        val text = view.message
        val time = view.time
        val photo = view.profile_photo
    }

}