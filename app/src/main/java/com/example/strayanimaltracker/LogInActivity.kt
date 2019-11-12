package com.example.strayanimaltracker

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class LogInActivity : AppCompatActivity() {

    private val MYTAG = "MYTAG"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()

        recuperarLoginPreference()

        login_login.setOnClickListener {
            verificarLogin()
        }

        cadastrar_login.setOnClickListener {
            startActivityForResult(Intent(this, SignUpActivity::class.java), 10)
        }
    }

    fun verificarLogin() {

        if(email_login.text!!.isEmpty()){
            email_login.error = "Campo não preenchido"
            return
        }else if (senha_login.text!!.isEmpty()) {
            senha_login.error = "Campo não preenchido"
            return
        }

        val email = email_login.text.toString()
        val senha = senha_login.text.toString()

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(MYTAG, "signInWithEmail:success")
                    login()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(MYTAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Falha na autenticação.",
                        Toast.LENGTH_SHORT).show()
                }

            }
    }

    fun salvarLoginPreference(email: String, senha: String){
        val sp = getSharedPreferences("com.example.strayanimaltracker", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("EMAIL_PREFERENCE", email)
        editor.putString("SENHA_PREFERENCE", senha)
        editor.apply()
    }

    fun recuperarLoginPreference(){
        val sp = getSharedPreferences("com.example.strayanimaltracker", Context.MODE_PRIVATE)
        email_login.setText(sp.getString("EMAIL_PREFERENCE", ""))
        senha_login.setText(sp.getString("SENHA_PREFERENCE", ""))
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            login()
        }
    }

    private fun login() {
        salvarLoginPreference(email_login.text.toString(), senha_login.text.toString())
        startActivity(Intent(this, MapsActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 10){
                recuperarLoginPreference()
            }
        }
    }

}
