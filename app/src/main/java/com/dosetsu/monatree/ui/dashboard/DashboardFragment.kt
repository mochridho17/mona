package com.dosetsu.monatree.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dosetsu.monatree.R
import com.dosetsu.monatree.controller.TambahGeofenceActivity
import com.dosetsu.monatree.databinding.FragmentDashboardBinding
import com.google.android.gms.location.Geofence

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var btnTambahGeofence: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // Inisialisasi tombol Tambah Geofence
        val btnTambahGeofence = view?.findViewById<Button>(R.id.btnGeofence)

        binding.btnGeofence.setOnClickListener {
            val intent = Intent(requireContext(), TambahGeofenceActivity::class.java)
            startActivity(intent)
        }

        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}