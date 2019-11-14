package com.example.strayanimaltracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private val LOGTAG = "LOGTAG"

    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastCoordinates: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getLocation()
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastCoordinates, 15F))
        }catch (e :Exception) {
            Log.e(LOGTAG,"Erro ao utilizar coordenadas.")
            e.printStackTrace()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
    }

    override fun onMyLocationButtonClick(): Boolean {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {}

    fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                val lat = location!!.latitude
                val lng = location.longitude
                lastCoordinates = LatLng(lat, lng)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao pegar a posição.", Toast.LENGTH_SHORT).show()
            }
    }

    private lateinit var auth: FirebaseAuth

    private fun logout() {
        auth.signOut()
        finish()
    }

    val CAMERA_REQUEST_CODE = 0

    fun abrirCamera() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CAMERA_REQUEST_CODE->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    //photoCapture.setImageBitmap(data.extras?.get("data") as Bitmap) //photoCapture é um ImageView

                }
            }
            else ->{
                Toast.makeText(this,"Erro request code",Toast.LENGTH_LONG).show()
            }
        }
    }

}
