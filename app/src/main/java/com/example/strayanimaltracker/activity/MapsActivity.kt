package com.example.strayanimaltracker.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.strayanimaltracker.R
import com.example.strayanimaltracker.entity.Post
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot


class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {

    private val LOGTAG = "LOGTAG"
    private val MARK_REQUEST_CODE = 10
    private val POST_REQUEST_CODE = 20

    private val marcacaoMapa: MutableMap<String, Marker> = HashMap()

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastCoordinates: LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        var toobar: Toolbar = findViewById(R.id.toolbar_maps)

        setSupportActionBar(toobar)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    // Infla as opções na barra de menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return true
        //return super.onCreateOptionsMenu(menu)
    }

    // Executa funções de acordo com o item clicado na ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_nova_postagem) {
            abrirPostActivity()
            return true
        } else if (id == R.id.action_minhas_postagens) {
//            startActivity(Intent(this, UserActivity::class.java))
            startActivityForResult(Intent(this, UserActivity::class.java), MARK_REQUEST_CODE)
            return true
        } else if (id == R.id.action_sair) {
            logout()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }


    // Executa quando o mapa estiver pronto
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnInfoWindowClickListener(this)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener(this)
        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker?): View? {
                val v = TextView(this@MapsActivity)
                if (marker != null)
                    v.text = "${marker.title} - ${marker.snippet}"
                return v
            }

            override fun getInfoWindow(marker: Marker?): View? {
                return null
            }

        })

        carregarMarcacoes()

        getLocation {
            focusCamera(lastCoordinates)
        }
    }

    // Executa quando clicado em algum ponto do mapa
    override fun onMapClick(coordinates: LatLng?) {
        if (coordinates != null)
            abrirPostActivity(coordinates.latitude, coordinates.longitude)
    }

    // Executa quando clicado no botão de zoom
    override fun onMyLocationButtonClick(): Boolean {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    // Executa quando clicado em uma marcação
    override fun onMarkerClick(marker: Marker?): Boolean {
        marker!!.showInfoWindow()
        return false
    }

    // Executa quando a janela de informação da marcação é clicada
    override fun onInfoWindowClick(marker: Marker) {
        val i = Intent(this, AnimalActivity::class.java)
        i.putExtra("idPostagem", marker.tag.toString())
        startActivity(i)
    }

    // Pega a localização atual e executa a função callback ao fim da task
    private fun getLocation(callback: () -> Unit = {}) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val lat = location!!.latitude
            val lng = location.longitude
            lastCoordinates = LatLng(lat, lng)
            callback.invoke()

        }.addOnFailureListener {
            Log.e(LOGTAG, "Erro ao pegar a posição.")
        }
    }

    // Foca a câmera na posição dada
    private fun focusCamera(position: LatLng) {
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15F))
        } catch (e: Exception) {
            Log.e(LOGTAG, "Erro ao focalizar a câmera.")
            e.printStackTrace()
        }
    }

    // Sai do usuário atual
    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Abre a PostActivity para executar as funções de tirar uma foto e criar um novo post
    private fun abrirPostActivity(latitude: Double = 0.0, longitude: Double = 0.0) {
        if ((latitude != 0.0) && (longitude != 0.0)) {
            val i = Intent(this, PostActivity::class.java)
            i.putExtra("latitude", latitude)
            i.putExtra("longitude", longitude)
            startActivityForResult(i, POST_REQUEST_CODE)
        } else if (lastCoordinates != null) {
            val i = Intent(this, PostActivity::class.java)
            i.putExtra("latitude", lastCoordinates.latitude)
            i.putExtra("longitude", lastCoordinates.longitude)
            startActivityForResult(i, POST_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Erro ao usar as coordenadas.", Toast.LENGTH_LONG).show()
        }
    }

    // Faz uma marcação no mapa
    private fun marcarMapa(postagem: Post) {
        marcacaoMapa[postagem.id] = mMap.addMarker(
            MarkerOptions()
                .position(LatLng(postagem.latitude, postagem.longitude))
                .title(postagem.nome)
                .snippet(postagem.data)
        ).apply {
            this.tag = postagem.id
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MARK_REQUEST_CODE){
            Log.i(LOGTAG, "ENTROU NO MARK_REQUEST_CODE")
            mMap.clear()
            carregarMarcacoes()
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                POST_REQUEST_CODE -> {
                    val postId = data!!.getStringExtra("idPost")
                    downloadPost(postId!!) { post ->
                        marcarMapa(post)
                    }
                }
            }
        }
    }

    // Faz o download de um post pelo seu ID e retorna um objeto Post referente ao ID
    private fun downloadPost(id: String, callback: (post: Post) -> Unit = {}) {
        val referencia = db.collection("post").document(id)
        referencia.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    callback.invoke(pegarPostdeDocument(document))
                } else {
                    Log.e(LOGTAG, "Erro ao fazer download de documento: documento inexistente.")
                }
            }
            .addOnFailureListener { e ->
                Log.e(LOGTAG, "Erro ao fazer download de documento: ${e.message}.")
            }
    }

    // Carrega as marcações de todos usuários
    private fun carregarMarcacoes() {
        db.collection("post")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    marcarMapa(pegarPostdeDocument(document))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(LOGTAG, "Error getting documents: ", exception)
            }
    }

    // Extrai um objeto Post de um objeto DocumentSnapshot
    private fun pegarPostdeDocument(document: DocumentSnapshot): Post {
        val postagem = Post()

        postagem.id = document.id
        postagem.nome = document.data!!["nome"] as String
        postagem.idUsuario = document.data!!["idUsuario"] as String
        postagem.sexo = document.data!!["sexo"] as String
        postagem.especie = document.data!!["especie"] as String
        postagem.data = document.data!!["data"] as String
        postagem.latitude = document.data!!["latitude"].toString().toDouble()
        postagem.longitude = document.data!!["longitude"].toString().toDouble()

        return postagem
    }


}
