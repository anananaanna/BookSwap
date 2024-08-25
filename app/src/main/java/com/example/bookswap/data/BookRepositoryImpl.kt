package com.example.bookswap.data

import android.net.Uri
import com.example.bookswap.model.Book
import com.example.bookswap.model.service.DatabaseService
import com.example.bookswap.model.service.StorageService
import com.example.bookswap.utils.await
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage

class BookRepositoryImpl : BookRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val storageInstance = FirebaseStorage.getInstance()

    private val databaseService = DatabaseService(firestoreInstance)
    private val storageService = StorageService(storageInstance)


    override suspend fun getAllBooks(): Resource<List<Book>> {
        return try{
            val snapshot = firestoreInstance.collection("books").get().await()
            val books = snapshot.toObjects(Book::class.java)
            Resource.Success(books)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun saveBookData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: LatLng
    ): Resource<String> {
        return try{
            val currentUser = firebaseAuth.currentUser
            if(currentUser!=null){
                val mainImageUrl = storageService.uploadBookMainImage(mainImage)
                val galleryImagesUrls = storageService.uploadBookGalleryImages(galleryImages)
                val geoLocation = GeoPoint(
                    location.latitude,
                    location.longitude
                )
                val book = Book(
                    userId = currentUser.uid,
                    description = description,
                    crowd = crowd,
                    mainImage = mainImageUrl,
                    galleryImages = galleryImagesUrls,
                    location = geoLocation
                )
                databaseService.saveBookData(book)
                databaseService.addPoints(currentUser.uid, 5)
            }
            Resource.Success("Uspesno sačuvani svi podaci o knjizari")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserBooks(uid: String): Resource<List<Book>> {
        return try {
            val snapshot = firestoreInstance.collection("books")
                .whereEqualTo("userId", uid)
                .get()
                .await()
            val books = snapshot.toObjects(Book::class.java)
            Resource.Success(books)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}