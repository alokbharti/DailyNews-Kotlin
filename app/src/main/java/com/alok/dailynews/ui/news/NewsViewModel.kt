package com.alok.dailynews.ui.news

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.database.NewsDatabaseDao
import com.alok.dailynews.models.LikedNewsItem
import com.alok.dailynews.models.NewsItem
import kotlinx.coroutines.*

class NewsViewModel(
    val database: NewsDatabaseDao,
    application: Application) : AndroidViewModel(application)  {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main+viewModelJob)
    private val newsRepo: NewsRepo = NewsRepo()

    fun getNewsItemList(imageUrl:String) :MutableLiveData<ArrayList<NewsItem>>{
        return newsRepo.getNewsData(imageUrl)
    }

    fun insertLikedNewsItem(likedNewsItem: LikedNewsItem){
        uiScope.launch {
            insert(likedNewsItem)
        }
    }

    private suspend fun insert(likedNewsItem: LikedNewsItem){
        Log.d("in", "insert fun")
        Log.d("in insert","likedNewsItem title: "+likedNewsItem.title)
        withContext(Dispatchers.IO){
            database.insert(likedNewsItem)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}