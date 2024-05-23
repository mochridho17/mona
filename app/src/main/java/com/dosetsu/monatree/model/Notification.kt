package com.dosetsu.monatree.model

import com.google.firebase.database.ServerValue

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Any = ServerValue.TIMESTAMP
)

