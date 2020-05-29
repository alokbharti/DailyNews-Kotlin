package com.alok.dailynews.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    val imageUrl = "https://newsapi.org/v2/top-headlines?language=en&page=1&apiKey=2012066be1c944409c701878d544b5fc"

    init {
        getNewsItemList(imageUrl)
        initializeLikedNewsItems()
    }

    fun getNewsItemList(imageUrl:String) {
        Log.d(TAG, "in getNewsItemList")
        newsItemList = newsRepo.getNewsData(imageUrl)
    }

    fun insertLikedNewsItem(likedNewsItem: LikedNewsItem){
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
        //likedNewsItems = database.getAllLikedNewsItem()
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

    fun updateLikedNewsItem(likedNewsItem: LikedNewsItem) {
        uiScope.launch {
            update(likedNewsItem)
        }
    }

    private suspend fun update(likedNewsItem: LikedNewsItem) {
        withContext(Dispatchers.IO) {
            database.update(likedNewsItem)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}