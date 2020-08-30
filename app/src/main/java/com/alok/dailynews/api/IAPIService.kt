package com.alok.dailynews.api

import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.models.NewsItem

interface IAPIService {
    fun getNewsData(imageUrl: String): MutableLiveData<ArrayList<NewsItem>>
}