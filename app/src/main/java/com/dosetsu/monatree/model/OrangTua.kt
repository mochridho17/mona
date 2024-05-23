package com.dosetsu.monatree.model

data class OrangTua(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val create_at: String = "",
    val anak: List<Anak> = emptyList(),
    val kode_verifikasi: String = "",
    val geofences: List<MyGeofence> = emptyList() // Daftar geofence yang dimiliki orang tua
)
