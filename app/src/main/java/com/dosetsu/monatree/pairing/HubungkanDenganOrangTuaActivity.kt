package com.dosetsu.monatree.pairing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dosetsu.monatree.AnakActivity
import com.dosetsu.monatree.R
import com.dosetsu.monatree.controller.LoginActivity
import com.dosetsu.monatree.databinding.ActivityHubungkanDenganOrangTuaBinding
import com.dosetsu.monatree.model.Anak
import com.dosetsu.monatree.model.HistoryLokasiAnak
import com.dosetsu.monatree.model.LokasiAnak
import com.dosetsu.monatree.model.OrangTua
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HubungkanDenganOrangTuaActivity : AppCompatActivity() {

    private lateinit var editTextKodeVerifikasi: EditText
    private lateinit var buttonHubungkan: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hubungkan_dengan_orang_tua)

        editTextKodeVerifikasi = findViewById(R.id.editTextKodeVerifikasi)
        buttonHubungkan = findViewById(R.id.buttonHubungkan)
        database = FirebaseDatabase.getInstance().reference

        buttonHubungkan.setOnClickListener {
            val kodeVerifikasi = editTextKodeVerifikasi.text.toString().trim()

            if (kodeVerifikasi.isEmpty()) {
                Toast.makeText(this, "Masukkan kode verifikasi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mendapatkan data anak yang sesuai dengan kode verifikasi
            hubungkanDenganOrangTua(kodeVerifikasi)
        }
    }

    private fun hubungkanDenganOrangTua(kodeVerifikasi: String) {
        // Mencari orang tua berdasarkan kode verifikasi
        database.child("orangTua").orderByChild("kode_verifikasi").equalTo(kodeVerifikasi).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Jika kode verifikasi valid, hubungkan orang tua dengan anak
                    for (orangTuaSnapshot in snapshot.children) {
                        val orangTuaId = orangTuaSnapshot.key
                        if (orangTuaId != null) {
                            tambahkanOrangTuaKeAnak(orangTuaId)
                        }
                    }
                } else {
                    Toast.makeText(this, "Kode verifikasi tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HubungkanActivity", "Error getting data", exception)
                Toast.makeText(this, "Gagal menghubungkan dengan orang tua", Toast.LENGTH_SHORT).show()
            }
    }

    private fun tambahkanOrangTuaKeAnak(orangTuaId: String) {
        // Mendapatkan referensi ke tabel Anak di Firebase Database
        val anakRef = database.child("Anak")

        // Mendapatkan semua anak dari database
        anakRef.get().addOnSuccessListener { anakSnapshot ->
            if (anakSnapshot.exists()) {
                // Untuk setiap anak, tambahkan orang tua ID
                for (anak in anakSnapshot.children) {
                    val anakId = anak.key
                    if (anakId != null) {
                        // Cek apakah anak ini sudah memiliki orang tua
                        anakRef.child(anakId).child("orangTuaId").get().addOnSuccessListener { orangTuaIdSnapshot ->
                            if (!orangTuaIdSnapshot.exists()) {
                                // Jika anak belum memiliki orang tua, tambahkan orang tua ID
                                anakRef.child(anakId).child("orangTuaId").setValue(orangTuaId)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Berhasil menghubungkan anak ke orang tua", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, AnakActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("HubungkanActivity", "Error updating orangTuaId on anak", exception)
                                        Toast.makeText(
                                            this,
                                            "Gagal menghubungkan anak ke orang tua",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                // Jika anak sudah memiliki orang tua, cek apakah orang tua yang baru sama dengan yang lama
                                if (orangTuaIdSnapshot.value.toString() != orangTuaId) {
                                    // Jika orang tua yang baru berbeda dengan yang lama, tambahkan orang tua ID baru
                                    anakRef.child(anakId).child("orangTuaId").setValue(orangTuaId)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Berhasil menghubungkan anak ke orang tua", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this, AnakActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("HubungkanActivity", "Error updating orangTuaId on anak", exception)
                                            Toast.makeText(
                                                this,
                                                "Gagal mengubah orang tua anak",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("HubungkanActivity", "Error getting orangTuaId from anak", exception)
                            Toast.makeText(this, "Gagal mengambil orang tua anak", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Tidak ada data anak", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("HubungkanActivity", "Error getting data from anak", exception)
            Toast.makeText(this, "Gagal menghubungkan anak ke orang tua", Toast.LENGTH_SHORT).show()
        }
    }
}
