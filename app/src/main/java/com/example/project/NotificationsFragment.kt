package com.example.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.discussion.NotificationsAdapter
import com.example.menu.MenuCustom
import com.example.notification.SwipeNotificationToDelete
import com.example.notification.*
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NotificationsFragment(
    val menu : MenuCustom
) : Fragment(), NotificationsAdapter.OnItemListener {
    private lateinit var sessionUser: SessionUser
    private var notifications : ArrayList<NotificationWithKey> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : NotificationsAdapter
    private lateinit var textView: TextView
    private lateinit var noResults : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        textView = view.findViewById(R.id.text)
        noResults = view.findViewById(R.id.silence)
        sessionUser = SessionUser(context!!)

        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(context!!)
        recyclerView.layoutManager = llm
        val itemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.HORIZONTAL)
        recyclerView.addItemDecoration(itemDecoration)

        val s = getString(R.string.notifications_all_read)
        textView.text = Html.fromHtml(s)
        //Set see to false for every notification
        textView.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("notifications/${sessionUser.getIdFromUser()}").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val notif = it.getValue(Notification::class.java)
                        val notifWithKey = NotificationWithKey(notif!!, it.key!!)
                        notifWithKey.seeNotification(context!!)
                    }
                }

            })
        }

        FirebaseDatabase.getInstance().getReference("notifications/${sessionUser.getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                searchNotifications()
            }

        })

        FirebaseDatabase.getInstance().getReference("discussions").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                menu.setBadges()
                //drawerMenu.setInfo()
            }

        })
        return view
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(sendBackText: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun searchNotifications(){
        notifications.clear()
        FirebaseDatabase.getInstance().getReference("notifications/${sessionUser.getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Bruh
            }

            override fun onDataChange(p0: DataSnapshot) {
                notifications.clear()

                val notifs = p0.children
                notifs.forEach {
                    val notif = it.getValue(Notification::class.java) as Notification
                    if(notif.type.key.isNotEmpty()){
                        println("OUPSI NOTIF $notif")
                        notif.isSeen = it.child("isSeen").value as Boolean
                        notifications.add(NotificationWithKey(notif, it.key!!))
                    }
                }

                if(notifications.size > 0){
                    var sortedList= ArrayList(notifications.sortedWith(compareBy({it.notification.date})))
                    sortedList.reverse()
                    notifications = sortedList
                    adapter = NotificationsAdapter(activity!!, context!!, R.layout.list_item_notification, sortedList, this@NotificationsFragment)
                    recyclerView.adapter = adapter
                    val itemTouchHelper = ItemTouchHelper(
                        SwipeNotificationToDelete(
                            context!!,
                            adapter
                        )
                    )
                    itemTouchHelper.attachToRecyclerView(recyclerView)
                    textView.visibility = View.VISIBLE
                    recyclerView.visibility = View.VISIBLE
                    noResults.visibility = View.GONE
                }
                else{
                    textView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    noResults.visibility = View.VISIBLE
                }
            }

        })
    }

    override fun onClick(position: Int) {
        if(notifications.size > 0){
            when(notifications[position].notification.type.typeNotif){
                TypeNotification.EVENT -> {
                    when(notifications[position].notification.type.action){
                        Action.ACCEPT, Action.INVITATION, Action.MODIFY -> {
                            notifications[position].seeNotification(context!!)
                            startActivity(Intent(context, EventInfoViewParticipantActivity::class.java).putExtra("key",notifications[position].notification.type.key ))
                        }
                        Action.WAIT -> {
                            notifications[position].seeNotification(context!!)
                            startActivity(Intent(context, EventInfoJojoActivity::class.java).putExtra("key",notifications[position].notification.type.key ))
                        }
                        else -> {
                            notifications[position].seeNotification(context!!)
                        }
                    }
                }
                TypeNotification.USER -> {
                    when(notifications[position].notification.type.action){
                        Action.ACCEPT, Action.INVITATION -> {
                            notifications[position].seeNotification(context!!)
                            startActivity(Intent(context, PublicUserActivity::class.java).putExtra("user",notifications[position].notification.type.key ))
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onLongClick(position: Int) {
        if(notifications.size > 0) {
            notifications[position].removeNotification(context!!)
        }
    }


}
