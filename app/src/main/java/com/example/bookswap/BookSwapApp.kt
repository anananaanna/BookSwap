package com.example.bookswap

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class BookSwapApp : Application() {
    val db by lazy { Firebase.firestore }
}