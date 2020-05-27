package com.alok.dailynews.ui.liked

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.database.NewsDatabaseDao
import com.alok.dailynews.models.LikedNewsItem
import kotlinx.coroutines.*

class LikedNewsViewModel (val database: NewsDatabaseDao,
                          application: Application) : AndroidViewModel(application)  {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var likedNewsItems: LiveData<List<LikedNewsItem>> = MutableLiveData()

    init {
        initializeLikedNewsItems()
    }

    private fun initializeLikedNewsItems() {
        likedNewsItems = database.getAllLikedNewsItem()
        /*uiScope.launch {
            likedNewsItems = getAllData()
        }*/
    }

    /*private suspend fun getAllData(): LiveData<List<LikedNewsItem>> {
        Log.d("in", "getAllData")
        return withContext(Dispatchers.IO) {
            database.getAllLikedNewsItem()
        }
    }*/

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