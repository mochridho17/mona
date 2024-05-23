package com.dosetsu.monatree.model

data class Anak(
    val id: String = "",
    val orangTuaId: String = "",
    val name: String = "",
    val age: String = "",
    val create_at: String = "",
    val isActive: Boolean = false,
    var lokasiAnak: LokasiAnak? = null,
    val locationHistory: HistoryLokasiAnak = HistoryLokasiAnak()
) {
    constructor() : this("", "", "", "", "", false, null, HistoryLokasiAnak())
}