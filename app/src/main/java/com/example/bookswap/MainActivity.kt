package com.example.bookswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bookswap.ui.theme.BookSwapTheme
import com.example.bookswap.viewmodels.AuthViewModel
import com.example.bookswap.viewmodels.AuthViewModelFactory
import com.example.bookswap.viewmodels.BookViewModel
import com.example.bookswap.viewmodels.BookViewModelFactory

class MainActivity : ComponentActivity() {
    private val userViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory()
    }
    private val bookViewModel: BookViewModel by viewModels{
        BookViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookSwap(userViewModel, bookViewModel)
        }
    }
}