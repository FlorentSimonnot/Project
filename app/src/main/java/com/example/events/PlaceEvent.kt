package com.example.events

import android.content.Context
import com.example.place.SessionGooglePlace
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import java.io.Serializable
import java.lang.Exception
import java.util.*

class PlaceEvent(
    var idPlace: String = "",
    var address : String = ""
 ) : Serializable {

    /**initAddress find the address in according to the place's id
     * @param context : the current context
     * @exception Exception : if google can't find address
     */
    fun initAddress(context: Context){
        val gg = SessionGooglePlace(context)
        gg.init()
        val placesClient = gg.createClient()

        val placeFields : List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS)
        val request : FetchPlaceRequest = FetchPlaceRequest.newInstance(idPlace, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                val place : Place = it.place
                address = place.address!!
            }
            .addOnFailureListener {
                throw Exception("ERROR, can't find address")
            }
    }

    override fun toString(): String {
        return "$idPlace AND $address"
    }

}