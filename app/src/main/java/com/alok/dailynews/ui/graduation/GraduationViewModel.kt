package com.alok.dailynews.ui.graduation

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.alok.dailynews.api.APIService
import com.alok.dailynews.api.responsemodel.CollegeListResponse
import com.alok.dailynews.utility.NetworkResult
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class GraduationViewModel : ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main+viewModelJob)
    private val collegeName = MutableLiveData<String>()
    val compressedFile = MutableLiveData<File>()

    init {
        loadCollegeMemories("IIITDM Jabalpur") //load data for first college
    }

    val collegeList:LiveData<List<CollegeListResponse>> = APIService.getCollegeList()

    fun loadCollegeMemories(college: String){
        collegeName.value = college
    }

    @ExperimentalCoroutinesApi
    val collegeMemoriesNetworkResult: LiveData<NetworkResult<Any>>
            = Transformations.switchMap(collegeName){
        APIService.getCollegeMemoriesNetworkResult(it).asLiveData()
    }

    val isMemorySubmitted: LiveData<Boolean> = APIService.isCollegeMemorySubmitted

    fun addCollegeMemory(imageFile:File, email:String, college:String, title:String){
        APIService.addCollegeMemory(imageFile, title, email, college)
    }

    fun getCompressedFile(context: Context, imageFile: Bitmap){
        uiScope.launch {
            compressedFile.value = getFile(context, imageFile)
        }
    }

    private suspend fun getFile(context: Context, imageFile: Bitmap):File{
        return withContext(Dispatchers.IO){
            //create a file to write bitmap data
            val f = File(context.cacheDir, "ImageFile")
            f.createNewFile()

            Log.d("GraduationViewModel", "original file size:${imageFile.byteCount}")

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            imageFile.compress(Bitmap.CompressFormat.JPEG, 20, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            Log.d("GraduationViewModel", "compressed file size:${imageFile.byteCount}")
            Log.d("GraduationViewModel", "compressed file size: ${f.length()}")
            f
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}