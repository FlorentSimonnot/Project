package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.media.Image
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
import kotlinx.android.synthetic.main.activity_modify_event.*
import kotlinx.android.synthetic.main.list_item_user_confirmed.*
import org.w3c.dom.Text
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList


open class HomeFragment(
    val menu :  MenuCustom
) : Fragment(), View.OnClickListener {

    private lateinit var progressBar : ProgressBar
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var lastLocation : Location
    private var currentLatLng = LatLng(.0, .0)

    private lateinit var activity: Activity
    private var events : ArrayList<EventWithDistance> = ArrayList()
    private lateinit var buttonCalendar : LinearLayout
    private lateinit var calendarLayout : LinearLayout
    private lateinit var calendarValue : java.util.Calendar
    private lateinit var textDay : TextView
    private lateinit var textMonth : TextView
    private var date : Calendar = Calendar()

    private lateinit var buttonPreviousMonth : ImageButton
    private lateinit var buttonNextMonth : ImageButton
    private lateinit var calendarViewCustom : CalendarViewCustom

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
        progressBar.visibility = View.GONE

        calendarLayout = view.findViewById(R.id.barMonth)
        buttonCalendar = view.findViewById(R.id.buttonCalendar)
        buttonPreviousMonth = view.findViewById(R.id.previousMonth)
        buttonNextMonth = view.findViewById(R.id.nextMonth)

        textDay = view.findViewById(R.id.textDay)
        textMonth = view.findViewById(R.id.textMonth)

        calendarValue = java.util.Calendar.getInstance()
        date.day = calendarValue.get(java.util.Calendar.DATE)
        date.month = calendarValue.get(java.util.Calendar.MONTH)
        date.year = calendarValue.get(java.util.Calendar.YEAR)

        calendarViewCustom = CalendarViewCustom(view, context!!)
        calendarViewCustom.init()
        calendarViewCustom.writeCurrentMonth()

        textDay.text = date.day.toString()
        textMonth.text = date.getMonth()

        buttonCalendar.setOnClickListener(this)
        buttonPreviousMonth.setOnClickListener(this)
        buttonNextMonth.setOnClickListener(this)

        buttonPreviousMonth.visibility = View.GONE


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

        showEventsNearLocation(context!!, listView!!, date)
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
        val ref = FirebaseDatabase.getInstance().getReference("events/${calendar.year}-${calendar.month}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    val event = it.getValue(Event::class.java) //Get event in a Event class
                    //Add event in list if it isn't null
                    if(event != null){
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
                events = ArrayList(events.sortedWith(compareBy{ it.event.date }).reversed())
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
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    override fun onStart() {
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
            R.id.buttonCalendar -> {
                calendarLayout.visibility = if(calendarLayout.visibility == View.GONE){
                    View.VISIBLE
                }else{
                    View.GONE
                }
            }
            R.id.previousMonth -> {
                calendarViewCustom.previousMonth()

                if(calendarViewCustom.currentDate.month == java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)){
                    buttonPreviousMonth.visibility = View.GONE
                }

            }
            R.id.nextMonth -> {
                calendarViewCustom.nextMonth()

                if(calendarViewCustom.currentDate.month > java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)){
                    buttonPreviousMonth.visibility = View.VISIBLE
                }

            }
        }
    }


}
