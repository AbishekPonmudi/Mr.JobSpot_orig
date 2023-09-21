package mrprogrammer.info.mrjobspot.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import java.util.*

class LocationManger(val context: Context) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var location: Location

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { l: Location? ->
            if (l != null) {
                location = l
            } else {
                getLocation()
            }
        }
    }

    private fun getLocation() {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 100
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult ?: return

                    for (i in locationResult.locations) {
                        location = i
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }


}