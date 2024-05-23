package com.dosetsu.monatree.pairing

import android.annotation.SuppressLint
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dosetsu.monatree.MainActivity
import com.dosetsu.monatree.R
import com.dosetsu.monatree.model.OrangTua
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Random

class KodePairingActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val TAG = "KodePairingActivity"
    private lateinit var tvKodeUnik: TextView
    private lateinit var buttonSelesai: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_kode_pairing)

        tvKodeUnik = findViewById(R.id.tvKodeUnik)
        buttonSelesai = findViewById(R.id.btnSelesai)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("orangTua")

        val uid = FirebaseAuth.getInstance().currentUser?.uid


        // Ambil kode_verifikasi dari database Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    if (data.key == uid) {
                        val userData = data.getValue(OrangTua::class.java)
                        if (userData != null) {
                            tvKodeUnik.text = userData.kode_verifikasi
                        }
                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error getting OrangTua data", databaseError.toException())
            }
        })


        buttonSelesai.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            setResult(RESULT_OK, intent)
            finish()
        }
    }


    companion object {
        const val REQUEST_CODE_PAIRING = 100
    }
}