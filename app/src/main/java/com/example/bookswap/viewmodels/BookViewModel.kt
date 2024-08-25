package com.example.bookswap.viewmodels

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookswap.data.BookRepositoryImpl
import com.example.bookswap.data.RateRepositoryImpl
import com.example.bookswap.data.Resource
import com.example.bookswap.model.Book
import com.example.bookswap.model.Rate
import com.example.bookswap.model.service.DatabaseService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {
    val repository = BookRepositoryImpl()
    val rateRepository = RateRepositoryImpl()

    private val _bookFlow = MutableStateFlow<Resource<String>?>(null)
    val bookFlow: StateFlow<Resource<String>?> = _bookFlow

    private val _newRate = MutableStateFlow<Resource<String>?>(null)
    val newRate: StateFlow<Resource<String>?> = _newRate

    private val _books = MutableStateFlow<Resource<List<Book>>>(Resource.Success(emptyList()))
    val books: StateFlow<Resource<List<Book>>> get() = _books

    private val _rates = MutableStateFlow<Resource<List<Rate>>>(Resource.Success(emptyList()))
    val rates: StateFlow<Resource<List<Rate>>> get() = _rates


    private val _userBooks = MutableStateFlow<Resource<List<Book>>>(Resource.Success(emptyList()))
    val userBooks: StateFlow<Resource<List<Book>>> get() = _userBooks

    init {
        getAllBooks()
    }

    fun getAllBooks() = viewModelScope.launch {
        _books.value = repository.getAllBooks()
    }

    fun saveBookData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: MutableState<LatLng?>
    ) = viewModelScope.launch{
        _bookFlow.value = Resource.loading
        repository.saveBookData(
            description = description,
            crowd = crowd,
            mainImage = mainImage,
            galleryImages = galleryImages,
            location = location.value!!
        )
        _bookFlow.value = Resource.Success("Uspešno dodata knjižara")
    }


    fun getBookAllRates(
        bid: String
    ) = viewModelScope.launch {
        _rates.value = Resource.loading
        val result = rateRepository.getBookRates(bid)
        _rates.value = result
    }

    fun addRate(
        bid: String,
        rate: Int,
        book: Book
    ) = viewModelScope.launch {
        _newRate.value = rateRepository.addRate(bid, rate, book)
    }

    fun updateRate(
        rid: String,
        rate: Int
    ) = viewModelScope.launch{
        _newRate.value = rateRepository.updateRate(rid, rate)
    }

    fun getUserBooks(
        uid: String
    ) = viewModelScope.launch {
        _userBooks.value = repository.getUserBooks(uid)
    }
}

class BookViewModelFactory:ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BookViewModel::class.java)){
            return BookViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}