package com.example.strayanimaltracker.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.strayanimaltracker.R
import com.example.strayanimaltracker.UserFragment
import com.example.strayanimaltracker.adapter.PostAdapter
import com.example.strayanimaltracker.dialog.ExclusaoDialog
import com.example.strayanimaltracker.entity.Post
import com.example.strayanimaltracker.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user.*
import java.lang.StringBuilder

class UserActivity : AppCompatActivity() {

    private val LOGTAG = "LOGTAG"

    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private lateinit var usuarioAtual: User

    private val postsDeletados = ArrayList<String>()

    private var userFragment: UserFragment? = null

    private var listaPost = ArrayList<Post>()
    var adapter = PostAdapter(listaPost, this::onClickCallback, this::onLongClickCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userFragment = supportFragmentManager.findFragmentById(R.id.frag_user) as UserFragment

        coletarDados()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun coletarDados() {
        pegarUsuario {
            pegarTodosPosts {
                quandoDadosForemColetados()
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
                    usuarioAtual.nome = document.get("nome") as String
                    usuarioAtual.sobrenome = document.get("sobrenome") as String
                    usuarioAtual.email = document.get("email") as String

                    callback.invoke()
                } else {
                    Log.e(LOGTAG, "No such document")
                }
            }
    }

    // Método pega todos os posts do usuário atual
    private fun pegarTodosPosts(callback: () -> Unit = {}) {
        db.collection("post")
            .whereEqualTo("idUsuario", usuarioAtual.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    listaPost.add(pegarPostdeDocument(document))
                }
                callback.invoke()
            }
            .addOnFailureListener { e ->
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
    private fun quandoDadosForemColetados() {
        initRecyclerView()
    }

    fun initRecyclerView() {

        adapter.posts = listaPost

        rvPosts.adapter = adapter

        val layoutMAnager = LinearLayoutManager(this)

        rvPosts.layoutManager = layoutMAnager

    }

    private fun onClickCallback(position: Int) {
        downloadImagem(listaPost[position].id) { imagem ->
            userFragment?.setImagem(imagem)
        }
    }

    private fun downloadImagem(id: String, callback: (imagem2: Bitmap) -> Unit = {}) {
        lateinit var imagem: Bitmap

        val storageRef = storage.reference
        val pathReference = storageRef.child("images/${usuarioAtual.id}/${id}.jpg")

        val ONE_MEGABYTE: Long = 1024 * 1024
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { image ->
            imagem = BitmapFactory.decodeByteArray(image, 0, image.size)

            callback.invoke(imagem)
        }.addOnFailureListener { e ->
            Log.e(LOGTAG, "Error getting documents: ", e.cause)
        }
    }

    fun onLongClickCallback(position: Int) {
        ExclusaoDialog.show(supportFragmentManager,
            object : ExclusaoDialog.OnExclusaoSetListener {
                override fun excluirPost() {
                    postsDeletados.add(listaPost[position].id)
                    removerPost(listaPost[position].id)
                    listaPost.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            })
    }

    fun removerPost(idPost: String) {
        deletarPost(idPost)
        deletarImagem(idPost)
        Toast.makeText(this, "Post deletado!", Toast.LENGTH_SHORT).show()
    }

    fun deletarPost(idPost: String) {
        db.collection("post").document(idPost)
            .delete()
            .addOnSuccessListener {
                Log.d(LOGTAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(LOGTAG, "Error deleting document", e)
            }
    }

    fun deletarImagem(idPost: String) {
        val storageRef = storage.reference
        var desertRef = storageRef.child("images/${usuarioAtual.id}/${idPost}.jpg")

        desertRef.delete().addOnSuccessListener {
            Log.d(LOGTAG, "Image successfully deleted!")
        }.addOnFailureListener { e ->
            Log.w(LOGTAG, "Error deleting image", e)
        }
    }
}
