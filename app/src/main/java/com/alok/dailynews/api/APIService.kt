package com.alok.dailynews.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.api.responsemodel.NewsResponse
import com.alok.dailynews.models.NewsItem
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.rx2androidnetworking.Rx2AndroidNetworking

object APIService: IAPIService{

    private const val TAG = "APIService"

    override fun getNewsData(imageUrl: String): MutableLiveData<ArrayList<NewsItem>> {
        val newsItemList: MutableLiveData<ArrayList<NewsItem>> = MutableLiveData()
        Rx2AndroidNetworking.get(imageUrl)
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsObject(NewsResponse::class.java, object : ParsedRequestListener<NewsResponse> {
                override fun onResponse(response: NewsResponse?) {
                    if (response != null) {
                        val tempList:ArrayList<NewsItem> = ArrayList()
                        for (data in response.newsData!!){
                            val newsItem = NewsItem(
                                data.title,
                                data.description,
                                data.imageUrl,
                                data.newsUrl,
                                data.newsSourceName.name)
                            tempList.add(newsItem)
                        }
                        newsItemList.value = tempList
                    } else {
                        newsItemList.value = null
                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError != null) {
                        Log.d(TAG, anError.message)
                    }
                    newsItemList.value = null
                }
            })

        return newsItemList
    }

}