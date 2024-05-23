package com.dosetsu.monatree.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dosetsu.monatree.MainActivity
import com.dosetsu.monatree.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        etEmail= findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        btnLogin.setOnClickListener {
            login()
        }

        setTextViewClickListener(this, R.id.tvRegister, RegisterActivity::class.java)
        setTextViewClickListener(this, R.id.tvLupaPassword, LupaPasswordActivity::class.java)
    }

    fun setTextViewClickListener(context: Context, textViewId: Int, destinationActivity: Class<*>) {
        val textView = findViewById<TextView>(textViewId)
        textView.setOnClickListener {
            val intent = Intent(context, destinationActivity)
            startActivity(intent)
        }

        fun setTextViewClickListenerLupasPassword(context: Context, textViewId: Int, destinationActivity: Class<*>) {
            val textView = findViewById<TextView>(textViewId)
            textView.setOnClickListener {
                val intent = Intent(context, destinationActivity)
                startActivity(intent)
            }
        }
    }

    private fun login() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua form", Toast.LENGTH_SHORT).show()

            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "Berhasil login: ${auth.currentUser?.uid}")
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w("LoginActivity", "Gagal login", task.exception)
                    Toast.makeText(this, "Gagal login. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}