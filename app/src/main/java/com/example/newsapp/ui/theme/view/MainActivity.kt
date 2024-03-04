package com.example.newsapp.ui.theme.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.newsapp.R
import com.example.newsapp.ui.theme.NewsRoute
import com.example.newsapp.ui.theme.NewsappTheme
import com.example.newsapp.ui.theme.model.Article
import com.example.newsapp.ui.theme.model.News
import com.example.newsapp.ui.theme.viewmodel.NewsViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private lateinit var newViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /***
         *  To manage the notification
         * */
        FirebaseMessaging
            .getInstance().token
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
            }

        newViewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        setContent {
            NewsappTheme {


                /**
                 * To Naviagte user from listscreen to
                 * details screen.
                 * **/
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NewsRoute.NewsLists.name
                ) {
                    composable(NewsRoute.NewsLists.name) {

                        /**
                         * To show list of news to the users
                         * **/
                        NewsListScreen(
                            news = newViewModel.news.collectAsState(initial = null).value,
                            onCLick = { url ->
                                navController.navigate(
                                    "${NewsRoute.NewsDetails.name}/${
                                        Uri.encode(
                                            if (url.contains("https")) url else url.replace(
                                                "http",
                                                "https"
                                            )
                                        )
                                    }"
                                )

                            },
                            onFilterClick = { newViewModel.sortNews(it) })
                    }
                    composable(
                        route = "${NewsRoute.NewsDetails.name}/{url}", arguments = listOf(
                            navArgument("url") { type = NavType.StringType })
                    ) {
                        /***
                         * To show Details when user clicks on one of the item
                         ** */
                        NewsDetails(url = it.arguments!!.getString("url")!!)
                    }
                }
            }
        }
    }


}


@Composable
fun NewsListScreen(news: News?, onCLick: (url: String) -> Unit, onFilterClick: (id: Int) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (news != null) {
            Column {
                Text(
                    text = "HeadLines",
                    fontFamily = FontFamily(Font(resId = R.font.regular)),
                    fontWeight = FontWeight(900),
                    fontSize = TextUnit(35f, TextUnitType.Sp),
                    modifier = Modifier.padding(
                        top = 6.dp,
                        bottom = 8.dp,
                        start = 4.dp
                    )
                )
                Row(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    FilterText(
                        text = stringResource(id = R.string.sort_by_date),
                        isSelected = news.isDateSelected
                    ) {
                        onFilterClick(it)
                    }
                    FilterText(
                        text = stringResource(id = R.string.sort_by_author),
                        isSelected = news.isAuthorSelected
                    ) {
                        onFilterClick(it)
                    }
                    FilterText(
                        text = stringResource(id = R.string.sort_by_source),
                        isSelected = news.isSourceSelected
                    ) {
                        onFilterClick(it)
                    }
                }
                LazyColumn {
                    items(items = news.articles) {
                        NewsItem(it, onCLick)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(54.dp)
                        .padding(8.dp),
                    color = Color.Black,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Composable
fun NewsItem(newsArticle: Article, onCLick: (url: String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .fillMaxSize()
            .wrapContentHeight()
            .clickable {
                onCLick.invoke(newsArticle.url)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AsyncImage(
                    model = newsArticle.urlToImage, contentDescription = null,
                    modifier = Modifier
                        .width(140.dp)
                        .height(80.dp),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = newsArticle.source.name,
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight(300),
                    fontSize = TextUnit(12f, TextUnitType.Sp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 9.dp)
            ) {
                Text(
                    text = newsArticle.title,
                    fontFamily = FontFamily(Font(resId = R.font.regular)),
                    fontWeight = FontWeight(800),
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    maxLines = 2
                )
                Text(
                    text = newsArticle.description,
                    modifier = Modifier.padding(top = 4.dp),
                    fontFamily = FontFamily(Font(resId = R.font.regular)),
                    fontWeight = FontWeight(400),
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    maxLines = 2
                )
                Text(
                    text = newsArticle.publishedAt,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight(300),
                    fontSize = TextUnit(12f, TextUnitType.Sp)
                )
            }
        }
    }
}

@Composable
fun FilterText(text: String, isSelected: Boolean, onFilterClick: (id: Int) -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
            .then(
                if (isSelected) Modifier.border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
                else Modifier
            )
            .padding(6.dp)
            .clickable {
                onFilterClick.invoke(
                    when {
                        text.contains("Date") -> 0
                        text.contains("Author") -> 1
                        else -> 2
                    }
                )

            },
        fontFamily = FontFamily(Font(resId = R.font.regular)),
        fontWeight = FontWeight(300),
        fontSize = TextUnit(12f, TextUnitType.Sp)
    )
}

@Preview
@Composable
fun Preview() {
    NewsappTheme {
    }
}
