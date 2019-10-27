package com.example.messages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dateCustom.DateUTC
import com.example.project.FullscreenImageActivity
import com.example.project.PrivateUserActivity
import com.example.project.PublicUserActivity
import com.example.session.SessionUser
import com.example.user.PrivacyAccount
import com.example.user.User
import com.example.utils.Image
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_message.view.*
import kotlinx.android.synthetic.main.list_item_message.view.time
import kotlinx.android.synthetic.main.list_item_message_image.view.*
import kotlinx.android.synthetic.main.search_list_item_user.view.profile_photo


class MessageAdapter(
    val context: Context,
    val resourceMessageMe : Int,
    val resourceMessageOther : Int,
    val resourceMessageImageMe : Int,
    val resourceMessageImageOther : Int,
    val messages : ArrayList<Message>,
    val recyclerView: RecyclerView
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private var selectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType){
            0 -> {
                val layoutInflater : LayoutInflater = LayoutInflater.from(context)
                val view : View = layoutInflater.inflate(resourceMessageOther , null )
                AddresseeMessage(view)
            }
            2 -> {
                val layoutInflater : LayoutInflater = LayoutInflater.from(context)
                val view : View = layoutInflater.inflate(resourceMessageMe , null )
                SenderMessage(view)
            }
            1 -> {
                val layoutInflater : LayoutInflater = LayoutInflater.from(context)
                val view : View = layoutInflater.inflate(resourceMessageImageOther , null )
                AddresseeMessageImage(view)
            }
            3 ->{
                val layoutInflater : LayoutInflater = LayoutInflater.from(context)
                val view : View = layoutInflater.inflate(resourceMessageImageMe , null )
                SenderMessageImage(view)
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
        //Message send
        return if(message.sender.key == SessionUser(context).getIdFromUser()){
            if(message.typeMessage == TypeMessage.TEXT){
                2
            } else{
                3
            }
        }
        //Message receive
        else{
            if(message.typeMessage == TypeMessage.TEXT){
                0
            }else{
                1
            }

        }
    }

    fun setSelected(position : Int){
        selectedItem = position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
        if(position == selectedItem){
            holder.itemView.isSelected = true
        }
    }

    open class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        open fun bind(message : Message) {}
    }

    inner class SenderMessage(view : View) : ViewHolder(view){

        override fun bind(message: Message) {
            text.text = message.text
            time.text = DateUTC(message.dateTime).showTime()
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

    inner class SenderMessageImage(view : View) : ViewHolder(view){

        override fun bind(message: Message) {
            time.text = DateUTC(message.dateTime).showTime()
            val refPhoto  = FirebaseStorage.getInstance().getReference("${message.urlPhoto}").downloadUrl
            refPhoto.addOnSuccessListener {
                Picasso.get().load(it).into(urlPhoto)
            }
            urlPhoto.setOnClickListener {
                val image = Image(message.urlPhoto, DateUTC(message.dateTime).showDate(), DateUTC(message.dateTime).showTime())
                context.startActivity(Intent(context, FullscreenImageActivity::class.java).putExtra("image", image))
            }
            urlPhoto.setOnLongClickListener {
                message.deleteMessage(context)
                true
            }
        }

        val urlPhoto = view.message_image
        val time = view.time
    }

    inner class AddresseeMessage(view : View) : ViewHolder(view){

        override fun bind(message: Message) {
            text.text = message.text
            time.text = DateUTC(message.dateTime).showTime()
            User().showPhotoUser(context, photo, message.sender.key)
            text.setOnClickListener {
                if(time.visibility == View.GONE){
                    time.visibility = View.VISIBLE
                }
                else{
                    time.visibility = View.GONE
                }
            }
            photo.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().getReference("users/${message.sender.key}")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //Nothing
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val user = p0.getValue(User::class.java)
                        if(user != null){
                            if(user.privacyAccount == PrivacyAccount.Public){
                                val intent = Intent(context, PublicUserActivity::class.java)
                                intent.putExtra("user", message.sender.key)
                                context.startActivity(intent)
                            }
                            else{
                                val intent = Intent(context, PrivateUserActivity::class.java)
                                intent.putExtra("user", message.sender.key)
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

    inner class AddresseeMessageImage(view : View) : ViewHolder(view){

        override fun bind(message: Message) {
            //text.text = message.text
            time.text = DateUTC(message.dateTime).showTime()
            User().showPhotoUser(context, photo, message.sender.key)
            val refPhoto  = FirebaseStorage.getInstance().getReference("${message.urlPhoto}").downloadUrl
            refPhoto.addOnSuccessListener {
                Picasso.get().load(it).into(urlPhoto)
            }
            urlPhoto.setOnClickListener {
                val image = Image(message.urlPhoto, DateUTC(message.dateTime).showDate(), DateUTC(message.dateTime).showTime())
                context.startActivity(Intent(context, FullscreenImageActivity::class.java).putExtra("image", image))
            }
            urlPhoto.setOnLongClickListener {
                message.deleteMessage(context)
                true
            }
            photo.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().getReference("users/${message.sender.key}")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        //Nothing
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val user = p0.getValue(User::class.java)
                        if(user != null){
                            if(user.privacyAccount == PrivacyAccount.Public){
                                val intent = Intent(context, PublicUserActivity::class.java)
                                intent.putExtra("user", message.sender.key)
                                context.startActivity(intent)
                            }
                            else{
                                val intent = Intent(context, PrivateUserActivity::class.java)
                                intent.putExtra("user", message.sender.key)
                                context.startActivity(intent)
                            }
                        }
                    }

                })
            }
        }

        val urlPhoto = view.message_image
        val time = view.time
        val photo = view.profile_photo
    }

}