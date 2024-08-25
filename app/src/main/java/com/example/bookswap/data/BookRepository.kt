package com.example.bookswap.data

import android.net.Uri
import com.example.bookswap.model.Book
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

interface BookRepository {

    suspend fun getAllBooks(): Resource<List<Book>>
    suspend fun saveBookData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: LatLng
    ): Resource<String>

    suspend fun getUserBooks(
        uid: String
    ): Resource<List<Book>>
}