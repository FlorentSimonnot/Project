package com.example.project

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dateCustom.DateCustom
import com.example.dateCustom.DateUTC
import com.example.dateCustom.TimeCustom
import com.example.dialog.BottomSheetDialogEventRecap
import com.example.events.Event
import com.example.events.EventAndMarker
import com.example.menu.MenuCustom
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.sport.SportWithBoolean
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


open class MapFragment(
    val  menu : MenuCustom
) : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    lateinit var map : GoogleMap
    lateinit var mapView : MapView
    lateinit var v : View
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var lastLocation : Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private var eventsAndMarkers = ArrayList<EventAndMarker>()
    private val sports : ArrayList<Sport> = ArrayList()
    private val events : ArrayList<Event> = ArrayList()
    private val eventsForMarker : ArrayList<Event> = ArrayList()

    val PERMISSION_LOCATION_REQUEST_CODE = 1
    val REQUEST_CHECK_SETTINGS = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_map, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map) as MapView

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
            }
        }

        createLocationRequest()

        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync(this)

        FirebaseDatabase
            .getInstance()
            .getReference("sports/${SessionUser(context!!).getIdFromUser()}/parameters")
            .addValueEventListener(object  : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    searchEvents()
                }

            })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_map, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_settings_sport -> {
                context!!.startActivity(Intent(context!!, SettingsSport::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(context!!,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_LOCATION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context!!,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(activity!!)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity!!, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    private fun placeMarkerOnMap(markerOptions: MarkerOptions, resource : Int?, title : String = "") {
        map.addMarker(markerOptions)
    }

    private fun searchEvents(){
        sports.clear()
        if(context == null){
            println("NULLLL")
        }
        val ref = FirebaseDatabase.getInstance().getReference("sports/${SessionUser(context!!).getIdFromUser()}/parameters")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each sports with boolean
                data.forEach {
                    val sportWithBoolean = SportWithBoolean(Sport.valueOf(it.key!!), it.value as Boolean)
                    if(sportWithBoolean.boolean){
                        sports.add(sportWithBoolean.sport)
                    }
                }
                searchEventsAux(sports)

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchEventsAux(sportsWantsSee : ArrayList<Sport>){
        events.clear()
        eventsAndMarkers.clear()
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.children //Children = each event
                data.forEach {
                    val event = it.getValue(Event::class.java) //Get event in a Event class
                    if(event != null){
                        if(event.creator != SessionUser(context!!).getIdFromUser() && sportsWantsSee.contains(event.sport)) {
                            val date = event.date
                            if(DateUTC(date).isAfterNow()){
                                events.add(event)
                            }
                        }
                    }
                }
                makeEventsOnMap(events)

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun makeEventsOnMap(events : ArrayList<Event>){
        //Remove all markers from the map
        map.clear()
        eventsAndMarkers.clear()
        events.forEach {
            val gg = SessionGooglePlace(context!!)
            gg.init()

            val event = it
            val placeId = event.place.idPlace
            val placesClient = gg.createClient()
            val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG)
            val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields)

            placesClient.fetchPlace(request)
                .addOnSuccessListener {
                    val place : Place = it.place
                    val coords : LatLng? = place.latLng
                    if(coords != null){
                        val markerOptions = MarkerOptions()
                            .icon(bitmapDescriptorFromVector(context!!, event.sport.getLogoForMarker()))
                            .position(coords)
                            .title(event.name)
                        eventsAndMarkers.add(EventAndMarker(event, markerOptions))
                        placeMarkerOnMap(markerOptions, event.sport.getLogoSport(), event.name)
                    }
                }
                .addOnFailureListener {
                }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorDrawableResourceId : Int) : BitmapDescriptor {
        val background = ContextCompat.getDrawable(context, R.drawable.ic_noun_map_marker_25579)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight);

        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(5, 10, vectorDrawable.intrinsicWidth +5, vectorDrawable.intrinsicHeight +10)
        val bitmap = Bitmap.createBitmap(background.intrinsicWidth, background.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
       if(p0 == null){
           return false
       }
        eventsForMarker.clear()
        eventsAndMarkers.forEach {
            if(it.marker.position == p0.position){
                eventsForMarker.add(it.event)
            }
        }
        val bottomSheetDialogEventRecap = BottomSheetDialogEventRecap(context!!, R.layout.bottom_sheet_layout, eventsForMarker)
        bottomSheetDialogEventRecap.show(fragmentManager!!, "CARDVIEW EVENT")
        return true
    }


}
