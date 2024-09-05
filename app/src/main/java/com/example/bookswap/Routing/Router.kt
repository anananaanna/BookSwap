package com.example.bookswap.Routing

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookswap.data.Resource
import com.example.bookswap.model.Book
import com.example.bookswap.model.CustomUser
import com.example.bookswap.screens.BookScreen
import com.example.bookswap.screens.IndexScreen
import com.example.bookswap.viewmodels.AuthViewModel
import com.example.bookswap.screens.LoginScreen
import com.example.bookswap.screens.RankingScreen
import com.example.bookswap.screens.RegisterScreen
import com.example.bookswap.screens.SettingScreen
import com.example.bookswap.screens.TableScreen
import com.example.bookswap.screens.UserProfileScreen
import com.example.bookswap.viewmodels.BookViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Router(
    viewModel: AuthViewModel,
    bookViewModel: BookViewModel
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.loginScreen) {
        composable(Routes.loginScreen){
            LoginScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(Routes.indexScreen){
            val booksResource = bookViewModel.books.collectAsState()
            val bookMarkers = remember {
                mutableListOf<Book>()
            }
            booksResource.value.let {
                when(it){
                    is Resource.Success -> {
                        bookMarkers.clear()
                        bookMarkers.addAll(it.result)
                    }
                    is Resource.loading -> {

                    }
                    is Resource.Failure -> {
                        Log.e("Podaci", it.toString())
                    }
                    null -> {}
                }
            }
            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                bookViewModel = bookViewModel,
                bookMarkers = bookMarkers
            )
        }
        composable(
            route = Routes.indexScreenWithParams + "/{isCameraSet}/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("isCameraSet") { type = NavType.BoolType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val isCameraSet = backStackEntry.arguments?.getBoolean("isCameraSet")
            val latitude = backStackEntry.arguments?.getFloat("latitude")
            val longitude = backStackEntry.arguments?.getFloat("longitude")

            val booksResource = bookViewModel.books.collectAsState()
            val bookMarkers = remember {
                mutableListOf<Book>()
            }
            booksResource.value.let {
                when(it){
                    is Resource.Success -> {
                        bookMarkers.clear()
                        bookMarkers.addAll(it.result)
                    }
                    is Resource.loading -> {

                    }
                    is Resource.Failure -> {
                        Log.e("Podaci", it.toString())
                    }
                    null -> {}
                }
            }

            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                bookViewModel = bookViewModel,
                isCameraSet = remember { mutableStateOf(isCameraSet!!) },
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(latitude!!.toDouble(), longitude!!.toDouble()), 17f)
                },
                bookMarkers = bookMarkers
            )
        }
        composable(
            route = Routes.indexScreenWithParams + "/{isCameraSet}/{latitude}/{longitude}/{books}",
            arguments = listOf(
                navArgument("isCameraSet") { type = NavType.BoolType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("books") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isCameraSet = backStackEntry.arguments?.getBoolean("isCameraSet")
            val latitude = backStackEntry.arguments?.getFloat("latitude")
            val longitude = backStackEntry.arguments?.getFloat("longitude")
            val booksJson = backStackEntry.arguments?.getString("books")
            val books = Gson().fromJson(booksJson, Array<Book>::class.java).toList()

            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                bookViewModel = bookViewModel,
                isCameraSet = remember { mutableStateOf(isCameraSet!!) },
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(latitude!!.toDouble(), longitude!!.toDouble()), 17f)
                },
                bookMarkers = books.toMutableList(),
                isFilteredParam = true
            )
        }
        composable(
            route = Routes.indexScreenWithParams + "/{books}",
            arguments = listOf(
                navArgument("books") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val booksJson = backStackEntry.arguments?.getString("books")
            val books = Gson().fromJson(booksJson, Array<Book>::class.java).toList()
            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                bookViewModel = bookViewModel,
                bookMarkers = books.toMutableList(),
                isFilteredParam = true
            )
        }
        composable(Routes.registerScreen){
            RegisterScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = Routes.bookScreen + "/{book}",
            arguments = listOf(
                navArgument("book"){ type = NavType.StringType }
            )
        ){backStackEntry ->
            val bookJson = backStackEntry.arguments?.getString("book")
            val book = Gson().fromJson(bookJson, Book::class.java)
            bookViewModel.getBookAllRates(book.id)
            BookScreen(
                book = book,
                navController = navController,
                bookViewModel = bookViewModel,
                viewModel = viewModel,
                books = null
            )
        }
        composable(
            route = Routes.bookScreen + "/{book}/{books}",
            arguments = listOf(
                navArgument("book"){ type = NavType.StringType },
                navArgument("books"){ type = NavType.StringType },
            )
        ){backStackEntry ->
            val booksJson = backStackEntry.arguments?.getString("books")
            val books = Gson().fromJson(booksJson, Array<Book>::class.java).toList()
            val bookJson = backStackEntry.arguments?.getString("book")
            val book = Gson().fromJson(bookJson, Book::class.java)
            bookViewModel.getBookAllRates(book.id)

            BookScreen(
                book = book,
                navController = navController,
                bookViewModel = bookViewModel,
                viewModel = viewModel,
                books = books.toMutableList()
            )
        }
        composable(
            route = Routes.userProfileScreen + "/{userData}",
            arguments = listOf(navArgument("userData"){
                type = NavType.StringType
            })
        ){backStackEntry ->
            val userDataJson = backStackEntry.arguments?.getString("userData")
            val userData = Gson().fromJson(userDataJson, CustomUser::class.java)
            val isMy = FirebaseAuth.getInstance().currentUser?.uid == userData.id
            UserProfileScreen(
                navController = navController,
                viewModel = viewModel,
                bookViewModel = bookViewModel,
                userData = userData,
                isMy = isMy
            )
        }
        composable(
            route = Routes.tableScreen + "/{books}",
            arguments = listOf(navArgument("books") { type = NavType.StringType })
        ){ backStackEntry ->
            val booksJson = backStackEntry.arguments?.getString("books")
            val books = Gson().fromJson(booksJson, Array<Book>::class.java).toList()
            TableScreen(books = books, navController = navController, bookViewModel = bookViewModel)
        }

        composable(Routes.settingsScreen){
            SettingScreen(navController = navController)
        }
        composable(Routes.rankingScreen){
            RankingScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}