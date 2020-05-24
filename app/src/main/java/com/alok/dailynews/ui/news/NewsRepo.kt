package com.alok.dailynews.ui.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.models.NewsItem
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.rx2androidnetworking.Rx2AndroidNetworking
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NewsRepo {
    val TAG = "NewsRepo"
    val newsItemList: MutableLiveData<ArrayList<NewsItem>> = MutableLiveData()

    fun getNewsData(imageUrl:String): MutableLiveData<ArrayList<NewsItem>> {
        Rx2AndroidNetworking.get(imageUrl)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d(TAG, "request successful")
                    try {
                        val responseJsonArray: JSONArray = response!!.getJSONArray("articles")
                        val tempList:ArrayList<NewsItem> = ArrayList();
                        for (i in 0 until responseJsonArray.length()) {
                            val articleObject = responseJsonArray.getJSONObject(i)
                            val title = articleObject.getString("title")
                            val desc = articleObject.getString("description")
                            val imageUrl = articleObject.getString("urlToImage")
                            val newsUrl = articleObject.getString("url")
                            val isBookmarked = false;

                            val newsArticle = NewsItem(
                                title,
                                desc,
                                imageUrl,
                                newsUrl,
                                isBookmarked
                            )
                            tempList.add(newsArticle)
                        }
                        newsItemList.value = tempList
                    }catch(e: JSONException){}
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