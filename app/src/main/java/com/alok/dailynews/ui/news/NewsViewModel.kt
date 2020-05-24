package com.alok.dailynews.ui.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alok.dailynews.models.NewsItem

class NewsViewModel: ViewModel() {
    val newsRepo: NewsRepo = NewsRepo()

    fun getNewsItemList(imageUrl:String) :MutableLiveData<ArrayList<NewsItem>>{
        return newsRepo.getNewsData(imageUrl)
    }
}