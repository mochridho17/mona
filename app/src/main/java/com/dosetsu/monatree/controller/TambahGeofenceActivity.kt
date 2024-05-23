package com.dosetsu.monatree.controller

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dosetsu.monatree.R
import com.dosetsu.monatree.model.MyGeofence
import com.dosetsu.monatree.model.OrangTua
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TambahGeofenceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val polygonPoints = mutableListOf<LatLng>()
    private var polyline: Polyline? = null
    private lateinit var database: DatabaseReference
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var orangTuaId: String // Variabel untuk menyimpan ID orang tua

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_geofence)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Initialize GeofencingClient
        geofencingClient = LocationServices.getGeofencingClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        val etGeofenceName = findViewById<EditText>(R.id.etGeofenceName)
        val btnSaveGeofence = findViewById<Button>(R.id.btnSaveGeofence)

        // Dapatkan ID orang tua dari intent atau lokasi penyimpanan lainnya
        orangTuaId = "orangTuaId1"

        btnSaveGeofence.setOnClickListener {
            val geofenceName = etGeofenceName.text.toString()
            when {
                geofenceName.isEmpty() -> Toast.makeText(this, "Please enter a name for the geofence", Toast.LENGTH_SHORT).show()
                polygonPoints.size < 3 -> Toast.makeText(this, "Please add at least 3 points to create a polygon", Toast.LENGTH_SHORT).show()
                else -> {
                    saveGeofence(geofenceName, polygonPoints)
                    addGeofence(geofenceName, polygonPoints)
                }
            }
        }
    }

    // Metode untuk menambahkan geofence ke daftar geofence orang tua
    private fun tambahGeofenceKeOrangTua(geofence: MyGeofence) {
        val orangTuaRef = database.child("orangtua").child(orangTuaId)

        orangTuaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val orangTua = dataSnapshot.getValue(OrangTua::class.java)
                orangTua?.let {
                    val updatedGeofences = it.geofences.toMutableList()
                    updatedGeofences.add(geofence)

                    // Update daftar geofence orang tua dengan geofence baru
                    orangTuaRef.child("geofences").setValue(updatedGeofences)
                        .addOnSuccessListener {
                            Toast.makeText(this@TambahGeofenceActivity, "Geofence added!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@TambahGeofenceActivity, "Failed to add geofence to parent", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TambahGeofenceActivity, "Error fetching parent data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLocation = LatLng(it.latitude, it.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }
        }

        mMap.setOnMapClickListener { latLng ->
            polygonPoints.add(latLng)
            polyline?.remove()
            val polylineOptions = PolylineOptions().addAll(polygonPoints)
            polyline = mMap.addPolyline(polylineOptions)
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap)
        }
    }

    private fun saveGeofence(name: String, points: List<LatLng>) {
        val id = database.push().key ?: return
        val currentTime = System.currentTimeMillis()
        val geofence = MyGeofence(
            id = id,
            name = name,
            polygon = points,
            radius = 0.0, // Update if you have a radius logic
            createdAt = currentTime,
            updatedAt = currentTime
        )

        database.child("geofences").child(id).setValue(geofence)
            .addOnSuccessListener {
                // Tambahkan geofence ke daftar geofence orang tua
                tambahGeofenceKeOrangTua(geofence)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save geofence", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(geofenceName: String, points: List<LatLng>) {
        val geofenceCenter = getPolygonCenterPoint(points)
        val radius = calculateGeofenceRadius(geofenceCenter, points)

        val geofence = com.google.android.gms.location.Geofence.Builder()
            .setRequestId(geofenceName)
            .setCircularRegion(geofenceCenter.latitude, geofenceCenter.longitude, radius.toFloat())
            .setExpirationDuration(com.google.android.gms.location.Geofence.NEVER_EXPIRE)
            .setTransitionTypes(com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER or com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencePendingIntent: PendingIntent by lazy {
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Toast.makeText(this@TambahGeofenceActivity, "Geofence added!", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(this@TambahGeofenceActivity, "Failed to add geofence", Toast.LENGTH_SHORT).show()
            }
        }
    }





    private fun getPolygonCenterPoint(polygonPoints: List<LatLng>): LatLng {
        var centroidLat = 0.0
        var centroidLng = 0.0
        for (point in polygonPoints) {
            centroidLat += point.latitude
            centroidLng += point.longitude
        }
        val totalPoints = polygonPoints.size
        return LatLng(centroidLat / totalPoints, centroidLng / totalPoints)
    }

    private fun calculateGeofenceRadius(center: LatLng, points: List<LatLng>): Double {
        var maxDistance = 0.0
        for (point in points) {
            val distance = haversineDistance(center, point)
            if (distance > maxDistance) {
                maxDistance = distance
            }
        }
        return maxDistance
    }

    private fun haversineDistance(point1: LatLng, point2: LatLng): Double {
        val R = 6371e3 // Earth radius in meters
        val lat1 = Math.toRadians(point1.latitude)
        val lat2 = Math.toRadians(point2.latitude)
        val deltaLat = Math.toRadians(point2.latitude - point1.latitude)
        val deltaLng = Math.toRadians(point2.longitude - point1.longitude)

        val a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c
    }
}

