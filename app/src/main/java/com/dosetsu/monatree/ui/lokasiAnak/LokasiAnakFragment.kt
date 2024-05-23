package com.dosetsu.monatree.ui.lokasiAnak

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.dosetsu.monatree.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LokasiAnakFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inisialisasi variabel lain yang dibutuhkan

        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        database = FirebaseDatabase.getInstance()

        // Periksa izin lokasi dan update lokasi anak
        checkLocationPermission()

        return inflater.inflate(R.layout.fragment_lokasi_anak, container, false)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(requireActivity(), "Izin lokasi tidak diberikan", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Simpan lokasi ke Firebase Realtime Database
                val childLocation = hashMapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                    "timestamp" to System.currentTimeMillis()
                )
                val currentUid = auth.currentUser?.uid ?: ""
                database.getReference("lokasi_anak").child(currentUid).setValue(childLocation)

                // Mendapatkan lokasi saat ini dan memperbarui koordinat anak secara real-time
                val locationManager =
                    requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        // Simpan lokasi ke Firebase Realtime Database
                        val childLocation = hashMapOf(
                            "latitude" to location.latitude,
                            "longitude" to location.longitude,
                            "timestamp" to System.currentTimeMillis()
                        )
                        database.getReference("lokasi_anak").child(currentUid).setValue(childLocation)
                    }

                    // Implementasikan method lainnya sesuai kebutuhan
                }

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
                )
            } else {
                Toast.makeText(requireActivity(), "Tidak dapat mendapatkan lokasi", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Ambil UID pengguna saat ini
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        // Referensi ke lokasi anak di Firebase Realtime Database
        val lokasiAnakRef = FirebaseDatabase.getInstance().getReference("lokasi_anak").child(currentUid)

        // Dapatkan data lokasi anak dari Firebase Realtime Database
        lokasiAnakRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val latitude = snapshot.child("latitude").value as? Double
                    val longitude = snapshot.child("longitude").value as? Double

                    if (latitude != null && longitude != null) {
                        val anakLocation = LatLng(latitude, longitude)
                        // Tambahkan marker di lokasi anak
                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(anakLocation)
                                .title("Lokasi Anak")
                        )
                        // Zoom ke marker lokasi anak
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(anakLocation, 15f))
                    }
                } else {
                    Toast.makeText(requireActivity(), "Lokasi anak tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LokasiAnakFragment", "Gagal mengambil lokasi anak", error.toException())
            }
        })
    }







    companion object {
        private const val PERMISSION_REQUEST_CODE = 1000

    }
}