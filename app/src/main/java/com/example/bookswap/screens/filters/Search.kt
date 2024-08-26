package com.example.bookswap.screens.filters

import com.example.bookswap.model.Book

fun searchBooksByDescription(
    books: MutableList<Book>,
    query: String
):List<Book>{
    val regex = query.split(" ").joinToString(".*"){
        Regex.escape(it)
    }.toRegex(RegexOption.IGNORE_CASE)
    return books.filter { book ->
        regex.containsMatchIn(book.description)
    }
}
