package com.example.strayanimaltracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        recuperarLoginPreference()

        cadastrar_login.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


    fun salvarLoginPreference(){
        val sp = getSharedPreferences("com.example.strayanimaltracker", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("EMAIL_PREFERENCE", email_login.text.toString())
        editor.putString("SENHA_PREFERENCE", senha_login.text.toString())
        editor.apply()
    }

    fun recuperarLoginPreference(){
        val sp = getSharedPreferences("com.example.strayanimaltracker", Context.MODE_PRIVATE)
        email_login.setText(sp.getString("EMAIL_PREFERENCE", ""))
        senha_login.setText(sp.getString("SENHA_PREFERENCE", ""))
    }

}
