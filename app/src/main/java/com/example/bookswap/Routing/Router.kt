package com.example.bookswap.Routing

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookswap.viewmodels.AuthViewModel
import com.example.bookswap.viewmodels.BookViewModel

@Composable
fun Router(
    viewModel: AuthViewModel,
    bookViewModel: BookViewModel
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.loginScreen) {
        composable(Routes.loginScreen)
        {
//            LoginScreen(
//                viewModel = viewModel,
//                navController = navController
//            )
        }
    }
}