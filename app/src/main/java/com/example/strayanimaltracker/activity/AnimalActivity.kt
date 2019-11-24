package com.example.strayanimaltracker.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.strayanimaltracker.R
import com.example.strayanimaltracker.entity.Post
import com.example.strayanimaltracker.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_animal.*

class AnimalActivity : AppCompatActivity() {

    private val LOGTAG = "LOGTAG"

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    private lateinit var usuarioAtual: User
    private var postagem = Post()
    private lateinit var imagem: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal)

        postagem.id = intent.getStringExtra("idPostagem")!!

        coletarDados()

    }

    private fun coletarDados(){
        pegarUsuario{
            pegarPosts{
                pegarImagem{
                    quandoDadosForemColetados()
                }
            }
        }
    }

    // Método pega o usuário atual
    private fun pegarUsuario(callback: () -> Unit = {}) {
        usuarioAtual = User(auth.currentUser!!.uid)
        db.collection("user")
            .document(usuarioAtual.id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    usuarioAtual.nome =  document.get("nome") as String
                    usuarioAtual.sobrenome = document.get("sobrenome") as String
                    usuarioAtual.email = document.get("email") as String

                    Log.i(LOGTAG, "--------${usuarioAtual.id}, ${usuarioAtual.nome},${usuarioAtual.sobrenome}, ${usuarioAtual.email}")

                    callback.invoke()
                } else {
                    Log.e(LOGTAG, "No such document")
                }
            }
    }

    // Método pega todos os posts do usuário atual
    private fun pegarPosts(callback: () -> Unit = {}) {
        db.collection("post")
            .document(postagem.id)
            .get()
            .addOnSuccessListener { document ->
                postagem = pegarPostdeDocument(document)
                callback.invoke()
            }
            .addOnFailureListener {e ->
                Log.e(LOGTAG, "Error getting documents: ", e.cause)
            }
    }

    private fun pegarImagem(callback: () -> Unit = {}){
        val storageRef = storage.reference
        val pathReference = storageRef.child("images/${usuarioAtual.id}/${postagem.id}.jpg")

        val ONE_MEGABYTE: Long = 1024 * 1024
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {image ->
            imagem = BitmapFactory.decodeByteArray(image, 0 , image.size)

            callback.invoke()
        }.addOnFailureListener {e ->
            Log.e(LOGTAG, "Error getting documents: ", e.cause)
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

    // Executa quando listaPost estiver completa
    private fun quandoDadosForemColetados(){
        //TODO FAÇA AQUI DENTRO TODOS OS SET DE IMAGEM E TEXTO DO LAYOUT
        Log.i(LOGTAG, "--------${postagem.id}, ${postagem.idUsuario}, ${postagem.nome},${postagem.data}, ${postagem.sexo}, ${postagem.especie}, ${postagem.latitude}, ${postagem.longitude}")
        imagem_animal.setImageBitmap(imagem)
        id_postagem.text = "Id Postagem: ${postagem.id}"
        id_usuario.text="Id Usuario: ${postagem.idUsuario}"
        nome.text="Nome: ${postagem.nome}"
        sexo.text="Sexo: ${postagem.sexo}"
        especie.text="Especie: ${postagem.especie}"
        latitude.text="Latitude: ${postagem.latitude}"
        longitude.text= "Longitude: ${postagem.longitude}"
        data.text="Data: ${postagem.data}"
    }


}
