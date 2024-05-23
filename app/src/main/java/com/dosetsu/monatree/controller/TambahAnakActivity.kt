package com.dosetsu.monatree.controller

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dosetsu.monatree.R
import com.dosetsu.monatree.model.Anak
import com.dosetsu.monatree.model.HistoryLokasiAnak
import com.dosetsu.monatree.model.LokasiAnak
import com.dosetsu.monatree.pairing.KodePairingActivity
import com.google.firebase.database.FirebaseDatabase

class TambahAnakActivity : AppCompatActivity() {

    lateinit var spinnerUmurAnak: Spinner
    lateinit var buttonNext: Button
    lateinit var editTextNamaAnak: EditText
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_anak)

        buttonNext = findViewById(R.id.buttonNext)
        spinnerUmurAnak = findViewById(R.id.spinnerUmurAnak)
        editTextNamaAnak = findViewById(R.id.etNamaAnak)

        database = FirebaseDatabase.getInstance()

        setupSpinner()
        setupButtonListener()
    }

    private fun setupSpinner() {
        // Inisialisasi spinner dengan rentang umur 4 hingga 18 tahun
        val umurAnakList = (4..18).map { it.toString() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, umurAnakList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUmurAnak.adapter = adapter
    }

    private fun setupButtonListener() {
        buttonNext.setOnClickListener {
            val namaAnak = editTextNamaAnak.text.toString()
            val umurAnak = spinnerUmurAnak.selectedItem.toString()

            if (isInputValid(namaAnak)) {
                tambahAnak(namaAnak, umurAnak)
            } else {
                Toast.makeText(this, "Nama Anak harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isInputValid(namaAnak: String): Boolean {
        return namaAnak.isNotBlank()
    }

    private fun tambahAnak(namaAnak: String, umurAnak: String) {
        val childRef = database.getReference("Anak")
        val childId = childRef.push().key
        val child = Anak(
            childId!!,
            "", // isi orangTuaId nanti saat pairing
            namaAnak,
            umurAnak,
            System.currentTimeMillis().toString(),
            false,
            LokasiAnak(),
            HistoryLokasiAnak()
        )

        childRef.child(childId).setValue(child)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil menambahkan anak", Toast.LENGTH_SHORT).show()
                navigateToNextActivity(namaAnak, umurAnak)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menambahkan anak", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToNextActivity(namaAnak: String, umurAnak: String) {
        val intent = Intent(this, KodePairingActivity::class.java).apply {
            putExtra("NAMA_ANAK", namaAnak)
            putExtra("UMUR_ANAK", umurAnak)
        }
        startActivity(intent)
    }

}