package com.dosetsu.monatree.controller

import android.util.Log
import com.dosetsu.monatree.model.Notification
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "Refreshed token: $token")

        // Simpan token ini ke server atau lakukan operasi lain sesuai kebutuhan
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle pesan FCM yang diterima di sini
        Log.d("FCM Message", "From: ${remoteMessage.from}")

        // Cek apakah pesan memiliki data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM Data", "Message data payload: ${remoteMessage.data}")

            // Lakukan sesuatu dengan data payload, jika diperlukan
            // Contoh: Simpan notifikasi ke Firebase Realtime Database
            saveNotificationToDatabase(remoteMessage.data)
        }

        // Cek apakah pesan memiliki payload notifikasi
        remoteMessage.notification?.let {
            Log.d("FCM Notification", "Message Notification Body: ${it.body}")
        }
    }

    private fun saveNotificationToDatabase(data: Map<String, String>) {
        val notificationRef = FirebaseDatabase.getInstance().getReference("notifications")
        val notificationId = UUID.randomUUID().toString()
        val notification = Notification(
            id = notificationId,
            title = data["title"] ?: "",
            message = data["message"] ?: ""
        )

        notificationRef.child(notificationId).setValue(notification)
            .addOnSuccessListener {
                Log.d("Firebase Database", "Notification saved successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase Database", "Failed to save notification: ${exception.message}")
            }
    }

}
