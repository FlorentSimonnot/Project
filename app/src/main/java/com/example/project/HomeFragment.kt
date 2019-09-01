package com.example.project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.dateCustom.DateCustom
import com.example.dateCustom.TimeCustom
import com.example.discussion.LatestMessageAdapter
import com.example.events.Event
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


open class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val listView = view.findViewById<ListView>(R.id.events_listview)
        val createEventButton : Button = view.findViewById(R.id.create_event)

        createEventButton.setOnClickListener {
            startActivity(Intent(context, CreateEventActivity::class.java))
        }

        showAllEvents(context!!, listView!!)
        return view
    }

    /*--------CUSTOM MENU FOR THIS FRAGMENT-------------------*/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_search -> {
                context!!.startActivity(Intent(context!!, SearchBarActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(sendBackText: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    private fun showAllEvents(context: Context, listView : ListView){
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                val events : ArrayList<Event> = ArrayList()
                data.forEach {
                    val event = it.getValue(Event::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if(event != null){
                        /*there is a pb in this block*/
                        val date = DateCustom(event.date)
                        val time = TimeCustom(event.time)
                        if(date.isEqual(DateCustom("01/01/1971").getCurrentDate())){
                            if(time.isAfter(TimeCustom("01:01").getCurrentTime())){
                                events.add(event)
                            }
                        }
                        else if(date.isAfter(DateCustom("01/01/1971").getCurrentDate())){
                            events.add(event)
                        }
                    }
                }
                val adapter = ArrayAdapterCustom(context, R.layout.my_list, events)
                listView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


}
