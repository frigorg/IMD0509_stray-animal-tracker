package com.example.strayanimaltracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private val LOGTAG = "LOGTAG"
    private val CAMERA_REQUEST_CODE = 1

    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentPhotoPath: String? = ""
    private lateinit var lastCoordinates: LatLng

    private lateinit var currentUserId: String

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        currentUserId = auth.currentUser!!.uid


        //abrirCamera()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)

        getLocation()
    }

    override fun onMyLocationButtonClick(): Boolean {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {}

    fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener {location ->
            val lat = location!!.latitude
            val lng = location.longitude
            lastCoordinates = LatLng(lat, lng)

            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastCoordinates, 15F))
            }catch (e :Exception) {
                Log.e(LOGTAG,"Erro ao utilizar coordenadas.")
                e.printStackTrace()
            }

        }.addOnFailureListener {
            Log.e(LOGTAG,"Erro ao pegar a posição.")
        }
    }

    private fun logout() {
        auth.signOut()
        finish()
    }

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
                    val imageBitmap = data.extras!!.get("data") as Bitmap

                    armazenarFoto(currentUserId, "teste1", imageBitmap)
                }
            }
            else ->{
                Toast.makeText(this,"Erro request code",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun armazenarFoto(userId: String, postId: String, image: Bitmap) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images")
        val userRef = imagesRef.child(userId)
        val fileName = "${postId}.jpg"
        val fileRef = userRef.child(fileName)

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = fileRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Falha durante o upload da imagem.", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "Upload de imagem concluído.", Toast.LENGTH_LONG).show()
        }
    }

}
