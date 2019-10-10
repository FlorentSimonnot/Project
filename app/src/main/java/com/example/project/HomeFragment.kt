package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.arrayAdapterCustom.ArrayAdapterCustom
import com.example.calendar.*
import com.example.calendar.Calendar
import com.example.dateCustom.DateUTC
import com.example.events.Event
import com.example.events.EventWithDistance
import com.example.menu.MenuCustom
import com.example.session.SessionUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import java.util.*
import kotlin.collections.ArrayList


open class HomeFragment(
    val menu :  MenuCustom
) : Fragment(), View.OnClickListener {

    private lateinit var progressBar : ProgressBar
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var lastLocation : Location
    private var currentLatLng = LatLng(.0, .0)
    private lateinit var barMonth : TextView
    private lateinit var backArrow : ImageButton
    private lateinit var nextArrow : ImageButton
    private val calendar = Calendar()

    private lateinit var horizontalCalendar : HorizontalCalendar
    private var isClickMonth : Boolean = false
    private var lastPosition : Int = 0
    private lateinit var activity: Activity
    private val events : ArrayList<EventWithDistance> = ArrayList()

    val REQUEST_PERMISSIONS_REQUEST_CODE = 1

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
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE


        barMonth = view.findViewById(R.id.calendarMonthYear)
        backArrow = view.findViewById(R.id.backArrow)
        nextArrow = view.findViewById(R.id.nextArrow)

        calendar.showMonthYear(barMonth)
        nextArrow.setOnClickListener(this)
        backArrow.setOnClickListener(this)

        //Recreate badges when discussion is updated
        FirebaseDatabase.getInstance().getReference("discussions").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                menu.setBadges()
            }

        })

        FirebaseDatabase.getInstance().getReference("notifications/${SessionUser(context!!).getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                menu.setBadges()
            }

        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        showEventsNearLocation(context!!, listView!!, calendar)

        val endDate = java.util.Calendar.getInstance()
        val dayMissing = endDate.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) - endDate.get(java.util.Calendar.DATE)
        endDate.add(java.util.Calendar.DATE, dayMissing)
        val startDate = java.util.Calendar.getInstance()
        startDate.add(java.util.Calendar.MONTH, 0)

        horizontalCalendar = HorizontalCalendar.Builder(view, R.id.calendarView)
            .range(startDate, endDate)
            .datesNumberOnScreen(5)
            .configure()
                .showBottomText(false)
                .formatTopText("EEE")
            .end()
            .build()

        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {

            override fun  onDateSelected(date : java.util.Calendar, position : Int) {
                calendar.day = date.get(java.util.Calendar.DATE)
                if(!isClickMonth) {
                    val dayOfThisMonth = java.util.Calendar.getInstance()
                    dayOfThisMonth.set(calendar.year, calendar.month, calendar.day)

                    if(dayOfThisMonth.getMaximum(java.util.Calendar.DAY_OF_MONTH) == date.get(java.util.Calendar.DATE)) {
                        if(position <= lastPosition) {
                            calendar.setPreviousMonth()
                            calendar.showMonthYear(barMonth)
                        }
                    } else if (date.get(java.util.Calendar.DATE) == 1) {
                        if(position > lastPosition) {
                            calendar.setNextMonth()
                            calendar.showMonthYear(barMonth)
                        }
                    }
                }
                else{
                    isClickMonth = false
                }
                lastPosition = position
                showEventsNearLocation(context!!, listView!!, calendar)
            }
        }


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

    private fun showEventsNearLocation(context: Context, listView: ListView, calendar: Calendar){
        FirebaseDatabase.getInstance().getReference("users/${SessionUser(context).getIdFromUser()}").addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    val distance = p0.child("radius").value as Long
                    showAllEvents(context, listView, distance*1000, calendar)
                }

            }
        )
    }

    private fun showAllEvents(context: Context, listView : ListView, distance : Long, calendar: Calendar){
        events.clear()
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    val event = it.getValue(Event::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if(event != null){
                        /*there is a pb in this block*/
                        val date = DateUTC(event.date)
                        if(date.getDayInt() == calendar.day && date.getMonthInt() == calendar.month) {
                            val coordsEvent = Location("")
                            coordsEvent.latitude = event.place.lat
                            coordsEvent.longitude = event.place.lng
                            val coordsCurrent = Location("")
                            coordsCurrent.latitude = currentLatLng.latitude
                            coordsCurrent.longitude = currentLatLng.longitude
                            if(coordsEvent.distanceTo(coordsCurrent) < distance) {
                                val eventWithDistance = EventWithDistance(event, coordsEvent.distanceTo(coordsCurrent))
                                events.add(eventWithDistance)
                            }
                        }

                    }
                }
                val adapter = ArrayAdapterCustom(context, activity!!, R.layout.my_list, events )
                listView.adapter = adapter
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(context!!,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(activity!!,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    public override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful && task.result != null) {
                    lastLocation = task.result!!
                    currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                } else {

                }
            }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
            Manifest.permission.ACCESS_COARSE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    startLocationPermissionRequest()
                })

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest()
        }
    }

    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int, listener: View.OnClickListener) {
        Toast.makeText(context!!, getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation()
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.action_settings,
                    View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.nextArrow -> {
                calendar.setNextMonth()
                calendar.showMonthYear(barMonth)

                val c = java.util.Calendar.getInstance()
                c.set(calendar.year, calendar.month, 1)
                horizontalCalendar.selectDate(
                    c,
                    true
                )
                isClickMonth = true
            }
            R.id.backArrow -> {
                calendar.setPreviousMonth()
                calendar.showMonthYear(barMonth)

                val c = java.util.Calendar.getInstance()
                c.set(calendar.year, calendar.month, 1)
                horizontalCalendar.selectDate(
                    c,
                    true
                )
                isClickMonth = true
            }
        }
    }

}
