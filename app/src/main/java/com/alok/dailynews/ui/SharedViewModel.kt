package com.alok.dailynews.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.BuildConfig
import com.alok.dailynews.R
import com.alok.dailynews.database.NewsDatabaseDao
import com.alok.dailynews.models.LikedNewsItem
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.news.NewsRepo
import com.alok.dailynews.utility.Constants
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
        val imageUrl = "https://newsapi.org/v2/top-headlines?language=en&apiKey="+
                application.baseContext.resources.getString(R.string.news_api_key)
        getNewsItemList(imageUrl)
        initializeLikedNewsItems()
    }

    fun getNewsItemList(imageUrl:String) {
        Log.d(TAG, "in getNewsItemList")
        newsItemList = newsRepo.getNewsData(imageUrl)
    }

    fun removeSwipedArticle(newsItem: NewsItem){
        newsItemList.value!!.remove(newsItem)
    }

    fun insertLikedNewsItem(newsItem: NewsItem){
        val likedNewsItem = LikedNewsItem(title = newsItem.title, description = newsItem.description,
            imageUrl = newsItem.imageUrl, newsUrl = newsItem.newsUrl, isBookmarked = 1)

        uiScope.launch {
            insert(likedNewsItem)
        }
    }

    private suspend fun insert(likedNewsItem: LikedNewsItem){
        Log.d(TAG,"in insert, likedNewsItem title: "+likedNewsItem.title)
        withContext(Dispatchers.IO){
            database.insert(likedNewsItem)
        }
    }

    var likedNewsItems: LiveData<List<LikedNewsItem>> = MutableLiveData()

    fun initializeLikedNewsItems() {
        Log.d(TAG, "initializeLikedNewsItem")
        uiScope.launch {
            likedNewsItems = getAllData()
        }
    }

    private suspend fun getAllData(): LiveData<List<LikedNewsItem>> {
        Log.d(TAG, "getAllData")
        return withContext(Dispatchers.IO) {
            database.getAllLikedNewsItem()
        }
    }

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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}