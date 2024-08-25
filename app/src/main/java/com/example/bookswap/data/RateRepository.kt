package com.example.bookswap.data

import com.example.bookswap.model.Book
import com.example.bookswap.model.Rate

interface RateRepository {
    suspend fun getBookRates(
        bid: String
    ): Resource<List<Rate>>
    suspend fun getUserRates(): Resource<List<Rate>>
    suspend fun getUserAdForBook(): Resource<List<Rate>>
    suspend fun addRate(
        bid: String,
        rate: Int,
        book: Book
    ): Resource<String>

    suspend fun updateRate(
        rid: String,
        rate: Int,
    ): Resource<String>
}