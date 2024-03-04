package com.example.newsapp.ui.theme.model

data class News(
    val articles: List<Article>,
    val status: String,
    val isDateSelected: Boolean = false,
    val isAuthorSelected: Boolean = false,
    val isSourceSelected: Boolean = false,
)