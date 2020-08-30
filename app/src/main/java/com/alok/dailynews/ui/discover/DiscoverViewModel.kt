package com.alok.dailynews.ui.discover

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alok.dailynews.api.APIService
import com.alok.dailynews.models.NewsItem

class DiscoverViewModel: ViewModel() {

    fun getSearchedNewsData(imageUrl: String): MutableLiveData<ArrayList<NewsItem>> {
        Log.d("DiscoverViewModel", "in getSearchedNewsData")
        return APIService.getNewsData(imageUrl)
    }
}