package com.dosetsu.monatree.model

data class LokasiAnak(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis() / 1000
)