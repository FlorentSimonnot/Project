package com.example.events

import android.content.Context
import com.example.place.SessionGooglePlace
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import java.io.Serializable
import java.lang.Exception
import java.util.*

class PlaceEvent(
    var idPlace: String = "",
    var address : String = "",
    var lat : Double = .0,
    var lng : Double = .0
 ) : Serializable {

    /**initAddress find the address in according to the place's id
     * @param context : the current context
     * @exception Exception : if google can't find address
     */
    fun initAddress(context: Context){
        val gg = SessionGooglePlace(context)
        gg.init()
        val placesClient = gg.createClient()

        val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(idPlace, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                val place : Place = it.place
                address = place.address!!
                lat = place.latLng!!.latitude
                lng = place.latLng!!.longitude
            }
            .addOnFailureListener {
                throw Exception("ERROR, can't find address")
            }
    }

    override fun toString(): String {
        return "$idPlace AND $address"
    }

}