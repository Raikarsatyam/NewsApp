package com.example.newsapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.NewsFetcher
import com.example.newsapp.ui.theme.model.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class NewsViewModel: ViewModel() {
    private val _news = MutableStateFlow<News?>(null)
    val news = _news.asStateFlow().filterNotNull()
    init { getNews() }


    /**
    * To fetch News From API
    * */
    private fun getNews() = viewModelScope.launch {
        NewsFetcher.getNews().collectLatest {
            _news.value = it
        }
    }

    /**
    * To sort News Based On Filter Selected
    * */
    fun sortNews(id: Int) = viewModelScope.launch(Dispatchers.Default) {
        _news.value = when (id) {
            0 -> {
                _news.value?.let {
                    it.copy(
                        articles = it.articles.sortedBy { it.publishedAt },
                        isDateSelected = true,
                        isAuthorSelected = false,
                        isSourceSelected = false
                    )
                }
            }

            1 -> {
                _news.value?.let {
                    it.copy(
                        articles = it.articles.sortedBy { it.author },
                        isDateSelected = false,
                        isAuthorSelected = true,
                        isSourceSelected = false
                    )
                }
            }

            else -> {
                _news.value?.let {
                    it.copy(
                        articles = it.articles.sortedBy { it.source.name },
                        isDateSelected = false,
                        isAuthorSelected = false,
                        isSourceSelected = true
                    )
                }
            }
        }
    }
}