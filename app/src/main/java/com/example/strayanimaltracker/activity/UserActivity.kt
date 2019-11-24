package com.example.strayanimaltracker.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.strayanimaltracker.R
import com.example.strayanimaltracker.adapter.PostAdapter
import com.example.strayanimaltracker.entity.Post
import com.example.strayanimaltracker.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    private val LOGTAG = "LOGTAG"

    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var usuarioAtual: User
    var adapter: PostAdapter? = null
    private var listaPost = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        coletarDados()

    }


    private fun coletarDados(){
        pegarUsuario{
            pegarTodosPosts{
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
                usuarioAtual.nome =  document.get("nome") as String
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
            .addOnFailureListener {e ->
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
    fun initRecyclerView() {

        rvTarefas.adapter = adapter

        val layoutMAnager = LinearLayoutManager(this)

        rvTarefas.layoutManager = layoutMAnager

    }
    private fun list() {

        adapter = PostAdapter(posts = this.listaPost  )
        rvTarefas.adapter = adapter

    }
    // Executa quando listaPost estiver completa
    private fun quandoDadosForemColetados(){
        //TODO FAÇA AQUI DENTRO TUDO QUE VC PRECISAR DEPOIS QUE OS POSTS FOREM PEGOS
        listaPost.forEach{
            Log.i(LOGTAG, "${it.nome},${it.idUsuario}, ${it.data}, ${it.especie}, ${it.sexo}, ${it.longitude}, ${it.latitude}")
        }
        list()
        initRecyclerView()
    }


}
