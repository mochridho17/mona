package com.dosetsu.monatree.model

import com.google.android.gms.maps.model.LatLng

data class MyGeofence(
    val id: String = "",
    val name: String = "",
    val polygon: List<LatLng> = emptyList(),
    val radius: Double = 0.0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

