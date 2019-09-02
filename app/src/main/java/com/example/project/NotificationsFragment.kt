package com.example.project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.discussion.NotificationsAdapter
import com.example.menu.MenuCustom
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
        sessionUser = SessionUser(context!!)

        recyclerView.setHasFixedSize(true)
        val llm = LinearLayoutManager(context!!)
        recyclerView.layoutManager = llm
        val itemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.HORIZONTAL)
        recyclerView.addItemDecoration(itemDecoration)

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
                    notif.isSeen = it.child("isSeen").value as Boolean
                    notifications.add(NotificationWithKey(notif, it.key!!))
                }
                var sortedList= ArrayList(notifications.sortedWith(compareBy({it.notification.dateTime.date.toString()}, {it.notification.dateTime.time.toString()})))
                sortedList.reverse()
                notifications = sortedList
                adapter = NotificationsAdapter(context!!, R.layout.list_item_notification, sortedList, this@NotificationsFragment)
                recyclerView.adapter = adapter
            }

        })
    }

    override fun onClick(position: Int) {
        if(notifications.size > 0){
            when(notifications[position].notification.type.typeNotif){
                TypeNotification.EVENT -> {
                    when(notifications[position].notification.type.action){
                        Action.ACCEPT -> {
                            notifications[position].seeNotification(context!!)
                            startActivity(Intent(context, EventInfoViewParticipantActivity::class.java).putExtra("key",notifications[position].notification.type.key ))
                        }
                        else -> {}
                    }
                }
                TypeNotification.USER -> {
                    when(notifications[position].notification.type.action){
                        Action.ACCEPT -> {
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
