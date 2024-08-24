package com.example.bookswap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bookswap.Routing.Router
import com.example.bookswap.viewmodels.AuthViewModel
import com.example.bookswap.viewmodels.BookViewModel

@Composable
fun BookSwap(
    viewModel: AuthViewModel,
    bookViewModel: BookViewModel
){
    Surface(modifier = Modifier.fillMaxSize()) {
        Router(viewModel, bookViewModel)
    }
}