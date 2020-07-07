package com.alok.dailynews.ui.graduation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alok.dailynews.BuildConfig
import com.alok.dailynews.models.GraduationItem
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.rx2androidnetworking.Rx2AndroidNetworking
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class GraduationRepo {
    private val TAG = "GraduationRepo"
    private val UPLOAD_URL = "https://dailynews-0b10.restdb.io/rest/upload"
    private val IMAGE_URL="https://dailynews-0b10.restdb.io/media"
    private val COLLEGE_URL="https://dailynews-0b10.restdb.io/rest/college"
    val isCollegeMemorySubmitted = MutableLiveData<Boolean>()

    fun getCollegeList(): MutableLiveData<ArrayList<String>> {
        val collegList = MutableLiveData<ArrayList<String>>()
        Rx2AndroidNetworking.get(COLLEGE_URL)
            .addHeaders("content-type","application/json")
            .addHeaders("x-apikey", BuildConfig.X_API_KEY)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object: JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    Log.d(TAG, "request successful")
                    try {
                        if (response != null) {
                            val tempList:ArrayList<String> = ArrayList()
                            for (i in 0 until response.length()) {
                                val articleObject = response.getJSONObject(i)
                                val collegeName = articleObject.getString("name")
                                tempList.add(collegeName)
                            }
                            Log.d(TAG, "college list size: "+tempList.size)
                            collegList.value = tempList
                        } else {
                            collegList.value = null
                        }
                    }catch(e: JSONException){}
                }

                override fun onError(anError: ANError?) {
                    if (anError != null) {
                        Log.d(TAG, anError.message)
                    }
                    collegList.value = null
                }
            })

        return collegList
    }

    fun getCollegeMemories(collegeName:String):MutableLiveData<ArrayList<GraduationItem>>{
        val graduationMemories = MutableLiveData<ArrayList<GraduationItem>>()
        val collegeQuery = JSONObject().put("college", collegeName)
        Rx2AndroidNetworking.get(UPLOAD_URL)
            .addHeaders("content-type","application/json")
            .addHeaders("x-apikey", BuildConfig.X_API_KEY)
            .addQueryParameter("q", collegeQuery.toString())
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object: JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    Log.d(TAG, "request successful")
                    try {
                        if (response!=null) {
                            val tempList: ArrayList<GraduationItem> = ArrayList()
                            for (i in 0 until response.length()) {
                                val graduationObject = response.getJSONObject(i)
                                val title = graduationObject.getString("title")
                                val image = graduationObject.getString("image")
                                tempList.add(GraduationItem(title, image))
                            }
                            Log.d(TAG, "memories size: " + tempList.size)
                            graduationMemories.value = tempList
                        } else {
                            graduationMemories.value = null
                        }
                    }catch(e: JSONException){}
                }

                override fun onError(anError: ANError?) {
                    if (anError != null) {
                        Log.d(TAG, anError.message)
                    }
                    graduationMemories.value = null
                }
            })

        return graduationMemories
    }

    fun addCollegeMemory(file: File, title:String, email:String, college:String){
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
                        Log.d(TAG, anError.message)
                    }
                    isCollegeMemorySubmitted.value = false
                }
            })
    }

    
}