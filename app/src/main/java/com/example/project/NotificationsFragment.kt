package com.example.project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.discussion.NotificationsAdapter
import com.example.notification.Notification
import com.example.notification.Notifications
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NotificationsFragment : Fragment(), NotificationsAdapter.OnItemListener {
    private lateinit var sessionUser: SessionUser
    private var notifications : ArrayList<Notification> = ArrayList()
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

        searchNotifications()
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
        FirebaseDatabase.getInstance().getReference("notifications/${sessionUser.getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                //Bruh
            }

            override fun onDataChange(p0: DataSnapshot) {
                val notifs = p0.children
                notifs.forEach {
                    notifications.add(it.getValue(Notification::class.java) as Notification)
                }
                var sortedList= ArrayList(notifications.sortedWith(compareBy({it.date}, {it.time})))
                sortedList.reverse()
                println("   NOTIFICATION 1 : ${sortedList[0]}")
                adapter = NotificationsAdapter(context!!, R.layout.list_item_notification, sortedList, this@NotificationsFragment)
                recyclerView.adapter = adapter
            }

        })
    }

    override fun onClick(position: Int) {
        //Bruh
    }

}
