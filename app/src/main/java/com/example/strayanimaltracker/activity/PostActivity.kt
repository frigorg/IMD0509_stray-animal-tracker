package com.example.strayanimaltracker.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.strayanimaltracker.R
import com.example.strayanimaltracker.entity.Post
import com.example.strayanimaltracker.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val LOGTAG = "LOGTAG"

    private val GATO: String = "GATO"
    private val CACHORRO: String = "CACHORRO"

    private val CAMERA_REQUEST_CODE = 1

    private lateinit var imagem: Bitmap

    private lateinit var idUsuario: String
    private var post: Post = Post()

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    // TODO QUANDO É APERTADO O BOTÃO DO CEL DE VOLTAR ENQUANTO A CÂMERA ESTÁ ABERTA, A ACTIVITY DEVE SER FINALIZADA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //Verifica se o usuário está logado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            idUsuario = auth.currentUser!!.uid
        } else {
            Toast.makeText(this, "Erro ao pegar id de usuário.", Toast.LENGTH_LONG).show()
            finish()
        }

        //Configura os botões radio
        especie_radiogroup_post.setOnCheckedChangeListener{ group, checkedId ->
            if(cachorro_radio_post.isChecked) {
                post.especie = CACHORRO
                cachorro_radio_post.background = getDrawable(R.drawable.dogiconpink)
            } else
                cachorro_radio_post.background = getDrawable(R.drawable.dogiconpb)

            if(gato_radio_post.isChecked) {
                post.especie = GATO
                gato_radio_post.background = getDrawable(R.drawable.caticonpink)
            } else
                gato_radio_post.background = getDrawable(R.drawable.caticonpb)
        }


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.sexo,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner_post.adapter = adapter
        }

        spinner_post.onItemSelectedListener = this

        // Botão de cancelar
        cancelar_post.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        // Botão de cadastrar
        cadastrar_post.setOnClickListener {
            if (validarCampos()){
                adicionarPost()
            }
        }

        abrirCamera()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    // Ação ao selecionar um item do Spinner
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        post.sexo = parent.getItemAtPosition(pos).toString()
    }

    // Abre a câmera esperando resultado
    private fun abrirCamera() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(packageManager) != null) {

            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){

            // Muda a foto da activity e guarda a imagem da câmera
            CAMERA_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val imagemBitmap = data.extras!!.get("data") as Bitmap

                    imagem_post.setImageBitmap(imagemBitmap)

                    imagem = imagemBitmap
                }
            }
            else ->{
                Toast.makeText(this,"Erro request code",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun validarCampos(): Boolean{
        if (nome_post.text!!.isEmpty()) {
            nome_post.error = "Campo nome obrigatório"
            nome_post.requestFocus()
            return false
        }else if(post.sexo == "-Sexo-" || post.sexo == "") {
            Toast.makeText(this, "Selecione um sexo.", Toast.LENGTH_LONG).show()
            return false
        }else if(post.especie == "") {
            Toast.makeText(this, "Selecione uma raça.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun adicionarPost() {

        post.nome = nome_post.text.toString()
        post.idUsuario = idUsuario

        val post = hashMapOf(
            "idUsuario" to post.idUsuario,
            "nome" to post.nome,
            "sexo" to post.sexo,
            "especie" to post.especie,
            "data" to dataAtual()
        )

        db.collection("post")
            .add(post)
            .addOnSuccessListener { documentReference ->
                armazenarFoto(documentReference.id)
                Log.d(LOGTAG, "Post adicionado com o ID: ${documentReference.id}.")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(LOGTAG, "Erro ao adicionar o post.", e)
            }
    }

    // Armazena a foto no Storage
    private fun armazenarFoto(idPost: String) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images")
        val userRef = imagesRef.child(idUsuario)
        val fileName = "${idPost}.jpg"
        val fileRef = userRef.child(fileName)

        val baos = ByteArrayOutputStream()
        imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = fileRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Falha durante o upload da imagem.", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "Upload de imagem concluído.", Toast.LENGTH_LONG).show()
        }
    }

    private fun dataAtual(): String {
        val dataAtual = Calendar.getInstance().time

        val formatador = SimpleDateFormat("dd/MM/yyyy HH:mm")

        return formatador.format(dataAtual)
    }

}
