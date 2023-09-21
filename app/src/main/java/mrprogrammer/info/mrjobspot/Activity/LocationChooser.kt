package mrprogrammer.info.mrjobspot.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivityLocationChooseBinding

class LocationChooser : AppCompatActivity(), OnMapReadyCallback {
    lateinit var root:ActivityLocationChooseBinding
    private var marker: Marker? = null
    var isUserMoving =false
    var latitude = 0.0
    var longitude = 0.0
    private val deviceLocation = MrContext.getLocation().location
    private var googleMap: GoogleMap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivityLocationChooseBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)
        initMap()
        root.back.setOnClickListener {
            onBackPressed()
        }

        root.save.setOnClickListener {
            if(googleMap != null) {
                val intent = Intent()
                intent.putExtra("lat", latitude.toString())
                intent.putExtra("lon", longitude.toString())
                intent.putExtra("add", root.address.text.toString())
                intent.putExtra("add1", root.address1.text.toString())
                setResult(RESULT_OK, intent)
                finish()
                LocalFunctions.activityAnimation(this,fromFront = false)
            }
        }

        root.mylocation.setOnClickListener {
            showMyLocation()
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        LocalFunctions.activityAnimation(this,false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        latitude = deviceLocation.latitude
        longitude = deviceLocation.longitude
        val location = LatLng(deviceLocation.latitude,deviceLocation.longitude)
        marker = googleMap.addMarker(MarkerOptions().position(location))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        updateMarkerLocation(googleMap)
        googleMap.setOnCameraMoveListener {
            updateMarkerLocation(googleMap)
        }
    }

    private fun showMyLocation() {
        if(googleMap == null) {
            return
        } else {
            val location = LatLng(deviceLocation.latitude,deviceLocation.longitude)
            googleMap?.clear()
            marker = googleMap?.addMarker(MarkerOptions().position(location))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            updateMarkerLocation(googleMap)
        }
    }

    private fun updateMarkerLocation(googleMap: GoogleMap?) {
        val cameraPosition = googleMap?.cameraPosition
        val newLocation = cameraPosition?.target
        if (marker == null) {
            val markerOptions = newLocation?.let { MarkerOptions().position(it) }
            marker = googleMap?.addMarker(markerOptions)
        } else {
            marker?.position = newLocation
        }

        updateLocation()


        googleMap?.setOnCameraIdleListener {
            GlobalScope.launch {
                val address = LocalFunctions.getAddress(this@LocationChooser,latitude.toString().toDouble(),longitude.toString().toDouble())
                if (address != null) {
                    if(address.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            root.address.text = address.get(0).getAddressLine(0)
                            root.address1.text = address.get(0).locality
                        }
                    }
                }
            }
        }
    }

    private fun updateLocation(){
        val markerLatLng = marker?.position
        latitude = markerLatLng?.latitude.toString().toDouble()
        longitude = markerLatLng?.longitude.toString().toDouble()
    }

}