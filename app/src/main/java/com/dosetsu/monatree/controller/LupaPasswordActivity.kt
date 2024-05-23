package com.dosetsu.monatree.controller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dosetsu.monatree.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LupaPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var etEmail: EditText
    private lateinit var btnResetPassword: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lupa_password)

        etEmail= findViewById(R.id.etEmail)
        btnResetPassword = findViewById(R.id.btnResetPassword)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        btnResetPassword.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = etEmail.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Masukkan alamat email Anda", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LupaPasswordActivity", "Email reset password berhasil dikirim ke $email")
                    Toast.makeText(this, "Email reset password berhasil dikirim. Silakan cek email Anda.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w("LupaPasswordActivity", "Gagal mengirim email reset password", task.exception)
                    Toast.makeText(this, "Gagal mengirim email reset password. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}