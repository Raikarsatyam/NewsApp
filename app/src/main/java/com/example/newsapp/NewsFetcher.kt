package com.example.newsapp

import com.example.newsapp.ui.theme.model.News
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object NewsFetcher {

    private const val NEWS_API_URL =
        "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"

    suspend fun getNews(): Flow<News> = flow {
        /**
        * To fetch data from server
        * **/
        val jsonResponse = fetchJsonResponse()
        /**
        * To parser the data using gson
        * */
        val news = gsonParser(jsonResponse)
        emit(news)
    }.flowOn(Dispatchers.IO)


    private suspend fun fetchJsonResponse() = withContext(Dispatchers.IO) {
        try {
            val httpUrlConnection: HttpURLConnection?
            val url = URL(NEWS_API_URL)
            httpUrlConnection = url.openConnection() as HttpURLConnection
            httpUrlConnection.apply {
                connectTimeout = 10000
                readTimeout = 10000
            }
            try {
                val responseCode = httpUrlConnection.responseCode
                if (responseCode != 200) throw IOException("The error from server $responseCode")
                val bufferedReader =
                    BufferedReader(InputStreamReader(httpUrlConnection.inputStream))
                bufferedReader.use { it.readText() }
            } finally {
                httpUrlConnection.disconnect()
            }
        } catch (e: IOException) {
            throw IOException("Failed to fetch news: ${e.message}", e)
        }
    }

    private suspend fun gsonParser(jsonString: String) = withContext(Dispatchers.Default) {
        try {
            Gson().fromJson(jsonString, News::class.java)
        } catch (e: Exception) {
            throw IOException("Failed to parse JSON response: ${e.message}", e)
        }
    }
}