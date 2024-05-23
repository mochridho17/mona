package com.dosetsu.monatree.model

data class HistoryLokasiAnak(
    val childId: String = "",
    val locationHistory: Map<String, LokasiAnak> = emptyMap()
)
