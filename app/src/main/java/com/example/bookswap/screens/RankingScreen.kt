package com.example.bookswap.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookswap.Routing.Routes
import com.example.bookswap.data.Resource
import com.example.bookswap.model.Book
import com.example.bookswap.model.CustomUser
import com.example.bookswap.screens.components.OtherPlaces
import com.example.bookswap.screens.components.firstThreePlaces
import com.example.bookswap.screens.components.mapFooter
import com.example.bookswap.viewmodels.AuthViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RankingScreen(
    viewModel: AuthViewModel,
    navController: NavController
){
    viewModel.getAllUserData()
    val allUsersResource = viewModel.allUsers.collectAsState()

    val allUsers = remember {
        mutableListOf<CustomUser>()
    }
    val bookMarkers = remember {
        mutableStateListOf<Book>()
    }

    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .padding(vertical = 25.dp, horizontal = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "RANG LISTA",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        LazyColumn(
            modifier = Modifier.padding(top = 70.dp)
        ) {
            item { firstThreePlaces(
                users = allUsers.take(3),
                navController = navController
            ) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            if(allUsers.count() > 3) {
                item { OtherPlaces(
                    users = allUsers.drop(3),
                    navController = navController
                ) }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            mapFooter(
                openAddNewBook = {
                },
                active = 2,
                onHomeClick = {
                    navController.navigate(Routes.indexScreen)
                },
                onTableClick = {
                    val booksJson = Gson().toJson(bookMarkers)
                    val encodedBooksJson = URLEncoder.encode(booksJson, StandardCharsets.UTF_8.toString())
                    navController.navigate("tableScreen/$encodedBooksJson")
                },
                onRankingClick = {},
                onSettingsClick = {
                    navController.navigate(Routes.settingsScreen)
                }
            )
        }
    }

    allUsersResource.value.let {
        when(it){
            is Resource.Failure -> {}
            is Resource.Success -> {
                allUsers.clear()
                allUsers.addAll(it.result.sortedByDescending { x -> x.points })
            }
            Resource.loading -> {}
            null -> {}
        }
    }
}