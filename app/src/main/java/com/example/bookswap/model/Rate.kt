package com.example.bookswap.model

import com.google.firebase.firestore.DocumentId

data class Rate (
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    var rate: Int = 0
)