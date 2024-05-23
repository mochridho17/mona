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
import com.dosetsu.monatree.R
import com.dosetsu.monatree.model.OrangTua
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_main)
        setTextViewClickListener(this, R.id.tvLogin, LoginActivity::class.java)

        btnRegister = findViewById(R.id.btnRegister)
        etNama = findViewById(R.id.etNama)
        etEmail= findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        btnRegister.setOnClickListener {
            register()
        }

    }
    fun setTextViewClickListener(context: Context, textViewId: Int, destinationActivity: Class<*>) {
        val textView = findViewById<TextView>(textViewId)
        textView.setOnClickListener {
            val intent = Intent(context, destinationActivity)
            startActivity(intent)
        }

    }

    private fun register() {
        val name = etNama.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua form", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "Berhasil registrasi: ${auth.currentUser?.uid}")
                    Toast.makeText(this, "Registrasi berhasil. Silahkan Login", Toast.LENGTH_SHORT).show()
                    val currentUser = auth.currentUser
                    saveOrangTuaToDatabase(currentUser)

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w("RegisterActivity", "Gagal registrasi", task.exception)
                    Toast.makeText(this, "Gagal registrasi. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun generateCode(): String {
        return (100000 + Math.random() * 900000).toInt().toString()
    }

    private fun saveOrangTuaToDatabase(user: FirebaseUser?) {
        if (user != null) {
            val kodeUnik = generateCode()
            val orangTua = OrangTua(
                id = user.uid,
                name = etNama.text.toString(),
                email = etEmail.text.toString(),
                password = etPassword.text.toString(),
                create_at = System.currentTimeMillis().toString(),
                kode_verifikasi = kodeUnik
            )

            val ref = database.getReference("orangTua")
            ref.child(user.uid).setValue(orangTua)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil registrasi dan menyimpan data ke Firebase Realtime Database", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.w("RegisterActivity", "Gagal menyimpan data ke Firebase Realtime Database", exception)
                    Toast.makeText(this, "Gagal menyimpan data ke Firebase Realtime Database. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}