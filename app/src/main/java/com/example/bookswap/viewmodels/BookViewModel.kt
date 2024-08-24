package com.example.bookswap.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookViewModel(): ViewModel() {
}

class BookViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BookViewModel::class.java)){
            return BookViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}