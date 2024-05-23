package com.dosetsu.monatree.controller


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceBroadcast", "Error: ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val geofenceId = geofencingEvent.triggeringGeofences[0].requestId
            Log.d("GeofenceBroadcast", "Entered geofence: $geofenceId")
            sendNotificationToFCMToken(context, "Anak memasuki $geofenceId") // Kirim notifikasi ke FCM
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val geofenceId = geofencingEvent.triggeringGeofences[0].requestId
            Log.d("GeofenceBroadcast", "Exited geofence: $geofenceId")
            sendNotificationToFCMToken(context, "Anak keluar dari $geofenceId") // Kirim notifikasi ke FCM
        }
    }

    private fun sendNotificationToFCMToken(context: Context, message: String) {
        // Inisialisasi layanan FirebaseMessaging
        val firebaseMessaging = FirebaseMessaging.getInstance()

        // Mendapatkan nama geofence dari Firebase Realtime Database
        val databaseRef = FirebaseDatabase.getInstance().getReference("geofences")
        databaseRef.child("geofenceId").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val geofenceName = snapshot.child("name").getValue(String::class.java) ?: ""

                // Buat pesan notifikasi
                val notificationMessage = RemoteMessage.Builder("FCM_TOKEN_YANG_DITARGETKAN")
                    .setMessageId(UUID.randomUUID().toString())
                    .setData(mapOf("geofenceName" to geofenceName, "message" to message))
                    .build()

                try {
                    // Kirim notifikasi ke FCM
                    firebaseMessaging.send(notificationMessage)
                    Log.d("sendNotification", "Notification sent successfully")
                } catch (e: Exception) {
                    Log.e("sendNotification", "Failed to send notification: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase Database", "Error retrieving geofence name: ${error.message}")
            }
        })
    }


}
