package com.dosetsu.monatree.ui.homeAnak

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dosetsu.monatree.R
import com.dosetsu.monatree.controller.LoginActivity
import com.dosetsu.monatree.databinding.FragmentHomeAnakBinding
import com.dosetsu.monatree.databinding.FragmentHomeBinding
import com.dosetsu.monatree.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeAnakFragment : Fragment() {

    private var _binding: FragmentHomeAnakBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var orangTuaId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeAnakViewModel =
            ViewModelProvider(this).get(HomeAnakViewModel::class.java)

        _binding = FragmentHomeAnakBinding.inflate(inflater, container, false)
        val root: View = binding.root

        orangTuaId = arguments?.getString("orangTuaId")

        if (savedInstanceState != null) {
            // Memulihkan status terhubung dengan orang tua
            orangTuaId = savedInstanceState.getString("orangTuaId")
            if (orangTuaId.isNullOrEmpty()) {
                // Jika referensi orang tua tidak ada, keluar dari sesi
                logout()
            }
        }

        binding.btnLogout.setOnClickListener {
            // Periksa apakah referensi orang tua masih ada
            if (orangTuaId.isNullOrEmpty()) {
                // Referensi orang tua tidak ada, boleh keluar dari sesi
                logout()
            } else {
                // Referensi orang tua masih ada, jangan keluar dari sesi
                Toast.makeText(requireContext(), "Anda masih terhubung dengan orang tua", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Menyimpan referensi orang tua
        outState.putString("orangTuaId", orangTuaId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logout() {
        // Hapus authentikasi Firebase
        FirebaseAuth.getInstance().signOut()

        // Pindah ke halaman login
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}