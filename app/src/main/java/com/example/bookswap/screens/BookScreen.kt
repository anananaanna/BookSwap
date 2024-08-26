package com.example.bookswap.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookswap.Routing.Routes
import com.example.bookswap.data.Resource
import com.example.bookswap.model.Book
import com.example.bookswap.model.Rate
import com.example.bookswap.screens.components.BookMainImage
import com.example.bookswap.screens.components.CustomBackButton
import com.example.bookswap.screens.components.CustomBookGallery
import com.example.bookswap.screens.components.CustomBookLocation
import com.example.bookswap.screens.components.CustomBookRate
import com.example.bookswap.screens.components.CustomCrowdIndicator
import com.example.bookswap.screens.components.CustomRateButton
import com.example.bookswap.screens.components.greyText
import com.example.bookswap.screens.components.greyTextBigger
import com.example.bookswap.screens.components.headingText
import com.example.bookswap.screens.dialogs.RateBookDialog
import com.example.bookswap.viewmodels.AuthViewModel
import com.example.bookswap.viewmodels.BookViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.math.RoundingMode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun BookScreen(
    navController: NavController,
    bookViewModel: BookViewModel,
    book: Book,
    viewModel: AuthViewModel,
    books: MutableList<Book>?
){
    val ratesResources = bookViewModel.rates.collectAsState()
    val newRateResource = bookViewModel.newRate.collectAsState()

    val rates = remember {
        mutableListOf<Rate>()
    }
    val averageRate = remember {
        mutableStateOf(0.0)
    }
    val showRateDialog = remember {
        mutableStateOf(false)
    }

    val isLoading = remember {
        mutableStateOf(false)
    }

    val myPrice = remember {
        mutableStateOf(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BookMainImage(book.mainImage)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            item{ CustomBackButton {
                if(books == null) {
                    navController.popBackStack()
                }else{
                    val isCameraSet = true
                    val latitude = book.location.latitude
                    val longitude = book.location.longitude

                    val booksJson = Gson().toJson(books)
                    val encodedBooksJson = URLEncoder.encode(booksJson, StandardCharsets.UTF_8.toString())
                    navController.navigate(Routes.indexScreenWithParams + "/$isCameraSet/$latitude/$longitude/$encodedBooksJson")
                }
            }}
            item{Spacer(modifier = Modifier.height(220.dp))}
            item{ CustomCrowdIndicator(crowd = book.crowd)}
            item{Spacer(modifier = Modifier.height(20.dp))}
            item{headingText(textValue = "Knjižara u blizini")}
            item{Spacer(modifier = Modifier.height(10.dp))}
            item{CustomBookLocation(location = LatLng(book.location.latitude, book.location.longitude))}
            item{Spacer(modifier = Modifier.height(10.dp))}
            item{CustomBookRate(average = averageRate.value)}
            item{Spacer(modifier = Modifier.height(10.dp))}
            item{greyTextBigger(textValue = book.description.replace('+', ' '))}
            item{Spacer(modifier = Modifier.height(20.dp))}
            item{Text(text = "Galerija knjižare", style= TextStyle(fontSize = 20.sp))};
//            item{ CustomCrowdIndicator(crowd = 1)}
            item{Spacer(modifier = Modifier.height(10.dp))}
            item { CustomBookGallery(images = book.galleryImages)}
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 15.dp, vertical = 20.dp)
        ) {
            CustomRateButton(
                enabled = if(book.userId == viewModel.currentUser?.uid) false else true,
                onClick = {
                    val rateExist = rates.firstOrNull{
                        it.bookId == book.id && it.userId == viewModel.currentUser!!.uid
                    }
                    if(rateExist != null)
                        myPrice.value = rateExist.rate
                    showRateDialog.value = true
                })
        }


        if(showRateDialog.value){
            RateBookDialog(
                showRateDialog = showRateDialog,
                rate = myPrice,
                rateBook = {

                    val rateExist = rates.firstOrNull{
                        it.bookId == book.id && it.userId == viewModel.currentUser!!.uid
                    }
                    if(rateExist != null){
                        isLoading.value = true
                        bookViewModel.updateRate(
                            rid = rateExist.id,
                            rate = myPrice.value
                        )
                    }else {
                        isLoading.value = true
                        bookViewModel.addRate(
                            bid = book.id,
                            rate = myPrice.value,
                            book = book
                        )
                    }
                },
                isLoading = isLoading
            )
        }
    }

    ratesResources.value.let {
        when(it){
            is Resource.Success -> {
                rates.addAll(it.result)
                var sum = 0.0
                for (rate in it.result){
                    sum += rate.rate.toDouble()
                }
                if(sum != 0.0) {
                    val rawPositive = sum / it.result.count()
                    val rounded = rawPositive.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                    averageRate.value = rounded
                }  else {}
            }
            is Resource.loading -> {

            }
            is Resource.Failure -> {
                Log.e("Podaci", it.toString())
            }
        }
    }
    newRateResource.value.let {
        when(it){
            is Resource.Success -> {
                isLoading.value = false

                val rateExist = rates.firstOrNull{rate ->
                    rate.id == it.result
                }
                if(rateExist != null){
                    rateExist.rate = myPrice.value
                }
            }
            is Resource.loading -> {
//                isLoading.value = false
            }
            is Resource.Failure -> {
                val context = LocalContext.current
                Toast.makeText(context, "Došlo je do greške prilikom ocenjivanja knjižare", Toast.LENGTH_LONG).show()
                isLoading.value = false
            }
            null -> {
                isLoading.value = false
            }
        }
    }
}