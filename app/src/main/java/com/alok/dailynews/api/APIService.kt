package com.alok.dailynews.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.BuildConfig
import com.alok.dailynews.api.responsemodel.CollegeListResponse
import com.alok.dailynews.api.responsemodel.NewsResponse
import com.alok.dailynews.api.responsemodel.GraduationItemResponse
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.utility.NetworkResult
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.rx2androidnetworking.Rx2AndroidNetworking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import java.io.File

object APIService: IAPIService{

    private const val TAG = "APIService"
    private const val UPLOAD_URL = "https://dailynews-0b10.restdb.io/rest/upload"
    private const val IMAGE_URL="https://dailynews-0b10.restdb.io/media"
    private const val COLLEGE_URL="https://dailynews-0b10.restdb.io/rest/college"

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
                        Log.d(TAG, "${anError.message}")
                    }
                    newsItemList.value = null
                }
            })

        return newsItemList
    }

    override fun getCollegeList(): LiveData<List<CollegeListResponse>> {
        val collegeList = MutableLiveData<List<CollegeListResponse>>()
        Rx2AndroidNetworking.get(COLLEGE_URL)
            .addHeaders("content-type","application/json")
            .addHeaders("x-apikey", BuildConfig.X_API_KEY)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObjectList(CollegeListResponse::class.java, object: ParsedRequestListener<List<CollegeListResponse>> {

                override fun onResponse(response: List<CollegeListResponse>?) {
                    if (response != null) {
                        collegeList.value = response
                    } else {
                        collegeList.value = null
                    }
                }
                override fun onError(anError: ANError?) {
                    if (anError != null) {
                        Log.d(TAG, "${anError.message}")
                    }
                    collegeList.value = null
                }
            })

        return collegeList
    }

    @ExperimentalCoroutinesApi
    override fun getCollegeMemoriesNetworkResult(collegeName: String): Flow<NetworkResult<Any>> =
        callbackFlow{
            offer(NetworkResult.Loading("Loading memories..."))
            val collegeQuery = JSONObject().put("college", collegeName)
            Rx2AndroidNetworking.get(UPLOAD_URL)
                .addHeaders("content-type","application/json")
                .addHeaders("x-apikey", BuildConfig.X_API_KEY)
                .addQueryParameter("q", collegeQuery.toString())
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(GraduationItemResponse::class.java, object: ParsedRequestListener<List<GraduationItemResponse>> {
                    override fun onResponse(response: List<GraduationItemResponse>?) {
                        if (response!=null){
                            offer(NetworkResult.Success(response))
                        } else {
                            offer(NetworkResult.Error("Unable to fetch data"))
                        }
                    }

                    override fun onError(anError: ANError?) {
                        if (anError != null) {
                            offer(NetworkResult.Error("Unable to fetch data"))
                        }
                    }
                })

            awaitClose()
        }

    val isCollegeMemorySubmitted = MutableLiveData<Boolean>()
    override fun addCollegeMemory(file: File, title:String, email:String, college:String){
        Rx2AndroidNetworking.upload(IMAGE_URL)
            .addHeaders("x-apikey", BuildConfig.X_API_KEY)
            .addMultipartFile("upload", file)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d(TAG, "in response body")
                    if (response!=null) {
                        val ids = response.getJSONArray("ids")
                        val imageId = ids.getString(0)
                        Log.d(TAG, "imageId: $imageId")
                        addMemory(imageId, title, email, college)
                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError != null) {
                        Log.d(TAG, "adding image error: $anError")
                    }
                }
            })
    }

    private fun addMemory(imageId: String, title:String, email:String, college:String) {
        val upload =
            JSONObject()
                .put("title", title)
                .put("email", email)
                .put("college", college)
                .put("image", ("$IMAGE_URL/$imageId"))
        Rx2AndroidNetworking.post(UPLOAD_URL)
            .addHeaders("content-type","application/json")
            .addHeaders("x-apikey", BuildConfig.X_API_KEY)
            .addJSONObjectBody(upload)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d(TAG, "in response body")
                    isCollegeMemorySubmitted.value = true
                }

                override fun onError(anError: ANError?) {
                    if (anError != null) {
                        Log.d(TAG, "${anError.message}")
                    }
                    isCollegeMemorySubmitted.value = false
                }
            })
    }
}