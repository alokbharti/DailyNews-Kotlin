package com.alok.dailynews.ui

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.BuildConfig
import com.alok.dailynews.database.NewsDatabaseDao
import com.alok.dailynews.models.LikedNewsItem
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.news.NewsRepo
import kotlinx.coroutines.*

class SharedViewModel(
    val database: NewsDatabaseDao,
    application: Application) : AndroidViewModel(application)  {

    private val TAG = "SharedViewModel"
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main+viewModelJob)
    private val newsRepo: NewsRepo = NewsRepo()
    var newsItemList: MutableLiveData<ArrayList<NewsItem>> = MutableLiveData()

    init {
        val imageUrl = "https://newsapi.org/v2/top-headlines?language=en&country=in&category=General&apiKey="+
                /*application.baseContext.resources.getString(R.string.news_api_key)*/BuildConfig.API_KEY
        getNewsItemList(imageUrl)
    }

    fun getNewsItemList(imageUrl:String) {
        Log.d(TAG, "in getNewsItemList")
        newsItemList = newsRepo.getNewsData(imageUrl)
    }

    fun removeSwipedArticle(newsItem: NewsItem){
        newsItemList.value!!.remove(newsItem)
    }

    fun updateNewsItemListFromSearch(searchNewsItemList: ArrayList<NewsItem>){
        newsItemList.value = searchNewsItemList
    }

    fun insertLikedNewsItem(newsItem: NewsItem){
        checkAndInsertNewsItem(newsItem)
    }

    private suspend fun insert(likedNewsItem: LikedNewsItem){
        Log.d(TAG,"in insert, likedNewsItem title: "+likedNewsItem.title)
        withContext(Dispatchers.IO){
            database.insert(likedNewsItem)
        }
    }

    var likedNewsItems = database.getAllLikedNewsItem()

    fun deleteLikedNewsItem(likedNewsItem: LikedNewsItem) {
        uiScope.launch {
            delete(likedNewsItem)
        }
    }

    private suspend fun delete(likedNewsItem: LikedNewsItem) {
        withContext(Dispatchers.IO) {
            database.delete(likedNewsItem)
        }
    }

    fun checkAndInsertNewsItem(newsItem: NewsItem) {
        uiScope.launch {
            var likedNewsItem = getNewsItemWithTitle(newsItem.title)
            if (likedNewsItem == null){
                val tempLikedNewsItem = LikedNewsItem(
                    title = newsItem.title,
                    description = newsItem.description,
                    imageUrl = newsItem.imageUrl,
                    newsUrl = newsItem.newsUrl,
                    newsSourceName = newsItem.newsSourceName,
                    isBookmarked = 1
                )
                uiScope.launch {
                    insert(tempLikedNewsItem)
                }
            }
        }
    }

    private suspend fun getNewsItemWithTitle(title: String): LikedNewsItem? {
        return withContext(Dispatchers.IO){
            database.getNewsArticleWithTitle(title)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}