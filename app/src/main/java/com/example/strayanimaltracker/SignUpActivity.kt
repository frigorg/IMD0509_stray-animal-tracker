package com.example.strayanimaltracker

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val MYTAG = "MYTAG"
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        cancelar_signup.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        cadastrar_signup.setOnClickListener {
            if(validarCampos())
                cadastrarUser()
        }
    }

    private fun cadastrarUser() {
        val nome = nome_signup.text.toString()
        val sobrenome = sobrenome_signup.text.toString()
        val email = email_signup.text.toString()
        val senha = senha_signup.text.toString()

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Usuário Cadastrado com Sucesso", Toast.LENGTH_LONG
                    ).show()

                    adicionarUser(auth.currentUser!!.uid, nome, sobrenome, email)

                    salvarLoginPreference(email, senha)

                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    val resposta = task.exception!!.message
                    Toast.makeText(
                        this@SignUpActivity,
                        resposta, Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun validarCampos(): Boolean {

        if(nome_signup.text!!.isEmpty()){
            nome_signup.error = "Campo nome obrigatório"
            nome_signup.requestFocus()
            return false
        }else if (sobrenome_signup.text!!.isEmpty()) {
            sobrenome_signup.error = "Campo sobrenome obrigatório"
            sobrenome_signup.requestFocus()
            return false
        }else if(email_signup.text!!.isEmpty()){
            email_signup.error = "Campo E-mail obrigatório"
            email_signup.requestFocus()
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email_signup.text.toString()).matches()) {
            email_signup.error = "E-mail inválido"
            email_signup.requestFocus()
            return false
        }else if (senha_signup.text!!.isEmpty()) {
            senha_signup.error = "Campo Senha obrigatório"
            senha_signup.requestFocus()
            return false
        }

        return true
    }

    private fun adicionarUser(id: String, nome: String, sobrenome: String, email: String) {
        val user = hashMapOf(
            "nome" to nome,
            "sobrenome" to sobrenome,
            "email" to email
        )

        db.collection("user")
            .document(id)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(MYTAG, "Usuário adicionado com o ID: $id")
            }
            .addOnFailureListener { e ->
                Log.w(MYTAG, "Erro ao adicionar o usuário", e)
            }
    }


    fun salvarLoginPreference(email: String, senha: String){
        val sp = getSharedPreferences("com.example.strayanimaltracker", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("EMAIL_PREFERENCE", email)
        editor.putString("SENHA_PREFERENCE", senha)
        editor.apply()
    }
}
