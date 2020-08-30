package com.alok.dailynews.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.api.responsemodel.CollegeListResponse
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.utility.NetworkResult
import kotlinx.coroutines.flow.Flow
import java.io.File

interface IAPIService {

    fun getNewsData(imageUrl: String): MutableLiveData<ArrayList<NewsItem>>

    fun getCollegeList(): LiveData<List<CollegeListResponse>>

    fun getCollegeMemoriesNetworkResult(collegeName: String): Flow<NetworkResult<Any>>

    fun addCollegeMemory(file: File, title:String, email:String, college:String)

}