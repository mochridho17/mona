package com.dosetsu.monatree.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dosetsu.monatree.R
import com.dosetsu.monatree.adapter.AnakSpinnerAdapter
import com.dosetsu.monatree.databinding.FragmentHomeBinding
import com.dosetsu.monatree.controller.TambahAnakActivity
import com.dosetsu.monatree.model.Anak
import com.dosetsu.monatree.model.LokasiAnak
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var anakSpinnerAdapter: AnakSpinnerAdapter
    private lateinit var anakList: MutableList<Anak>
    private lateinit var database: FirebaseDatabase
    private lateinit var spinner: Spinner
    private lateinit var orangTuaId: String
    private lateinit var anakRef: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var btnHapusAnak: Button

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase and data
        anakList = mutableListOf()
        database = FirebaseDatabase.getInstance()
        orangTuaId = FirebaseAuth.getInstance().currentUser!!.uid

        spinner = binding.spinnerAnak
        anakSpinnerAdapter = AnakSpinnerAdapter(requireContext(), anakList)
        spinner.adapter = anakSpinnerAdapter
        btnHapusAnak = binding.btnHapusAnak
        btnHapusAnak.visibility = View.GONE

        // Set up the listener for spinner item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val anak = anakList[position]
                // Handle the selected child
                Toast.makeText(requireContext(), "Anak yang dipilih: ${anak.name}", Toast.LENGTH_SHORT).show()
                btnHapusAnak.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                btnHapusAnak.visibility = View.GONE
            }
        }

        binding.btnTambahAnak.setOnClickListener {
            navigateToTambahAnak()
        }

        loadAnakListFromFirebase()

        btnHapusAnak.setOnClickListener { onHapusAnakClicked(it) }

        return root
    }

    private fun loadAnakListFromFirebase() {
        anakRef = database.getReference("Anak")
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                anakList.clear()
                for (anakSnapshot in snapshot.children) {
                    val anak = anakSnapshot.getValue(Anak::class.java)
                    if (anak != null) {
                        anakList.add(anak)
                    }
                }
                anakSpinnerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(requireContext(), "Gagalmemuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        anakRef.orderByChild("orangTuaId").equalTo(orangTuaId).addValueEventListener(valueEventListener)
    }

    private fun navigateToTambahAnak() {
        val intent = Intent(requireContext(), TambahAnakActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove Firebase listener to prevent memory leaks
        anakRef.removeEventListener(valueEventListener)
        _binding = null
    }

    private fun deleteAnak(anak: Anak) {
        val anakRef = FirebaseDatabase.getInstance().reference.child("Anak").child(anak.id)
        anakRef.removeValue()
            .addOnSuccessListener {
                // Hapus anak dari daftar anak
                anakList.remove(anak)

                // Perbarui adapter spinner
                anakSpinnerAdapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), "Ponsel anak berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal menghapus ponsel anak: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmationDialog(anak: Anak) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Ponsel Anak")
            .setMessage("Apakah Anda yakin ingin menghapus Ponsel ${anak.name}?")
            .setPositiveButton("Ya") { _, _ ->
                deleteAnak(anak)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    fun onHapusAnakClicked(view: View) {
        val position = spinner.selectedItemPosition
        if (position != -1) {
            val anak = anakList[position]
            showDeleteConfirmationDialog(anak)
        } else {
            Toast.makeText(requireContext(), "Silakan pilih anak terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }

}