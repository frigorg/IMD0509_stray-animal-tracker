package com.example.strayanimaltracker.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.strayanimaltracker.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import com.example.strayanimaltracker.entity.*
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private val LOGTAG = "LOGTAG"

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
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

        abrirPostActivity()
    }

    // Executa quando o mapa estiver pronto
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)

        getLocation {
            focusCamera(lastCoordinates)
        }
    }

    // Executa quando clicado no botão do mapa
    override fun onMyLocationButtonClick(): Boolean {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    // Executa quando clicado na sua localização atual
    override fun onMyLocationClick(location: Location) {}

    // Pega a localização atual e executa a função callback ao fim da task
    private fun getLocation(callback: () -> Unit = {}) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation

        fusedLocationClient.lastLocation.addOnSuccessListener {location ->
            val lat = location!!.latitude
            val lng = location.longitude
            lastCoordinates = LatLng(lat, lng)
            callback.invoke()

        }.addOnFailureListener {
            Log.e(LOGTAG,"Erro ao pegar a posição.")
        }
    }

    // Foca a câmera na posição dada
    private fun focusCamera(position: LatLng) {
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15F))
        }catch (e :Exception) {
            Log.e(LOGTAG,"Erro ao focalizar a câmera.")
            e.printStackTrace()
        }
    }

    // Sai do usuário atual
    private fun logout() {
        auth.signOut()
        finish()
    }

    private fun abrirPostActivity() {
        val i = Intent(this, PostActivity::class.java)
        startActivity(i)
    }



//    private fun pegarIdUsuario() {
//        usuarioAtual = User(auth.currentUser!!.uid)
//        // Pega os dados do usuário
//        db.collection("user")
//            .document(usuarioAtual.id)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    usuarioAtual.nome =  document.get("nome") as String
//                    usuarioAtual.sobrenome = document.get("sobrenome") as String
//                    usuarioAtual.email = document.get("email") as String
//
//                    post.idUsuario = usuarioAtual.id
//
//                    Toast.makeText(this, "${usuarioAtual.id}, ${usuarioAtual.nome}, ${usuarioAtual.sobrenome}, ${usuarioAtual.email}", Toast.LENGTH_LONG).show()
//                } else {
//                    Log.e(LOGTAG, "No such document")
//                }
//            }
//    }

}
