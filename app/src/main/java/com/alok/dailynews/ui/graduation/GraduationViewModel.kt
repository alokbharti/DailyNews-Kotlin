package com.alok.dailynews.ui.graduation

import android.content.Context
import android.graphics.Bitmap
import android.media.ExifInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.alok.dailynews.models.GraduationItem
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class GraduationViewModel : ViewModel() {
    val graduationRepo = GraduationRepo()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main+viewModelJob)
    private val collegeName = MutableLiveData<String>()
    val compressedFile = MutableLiveData<File>()

    init {
        loadCollegeMemories("IIITDM Jabalpur")
    }

    val collegeList:LiveData<ArrayList<String>> = graduationRepo.getCollegeList()

    fun loadCollegeMemories(college: String){
        collegeName.value = college
    }

    val collegeMemories: LiveData<ArrayList<GraduationItem>> = Transformations.switchMap(collegeName){
        graduationRepo.getCollegeMemories(it)
    }

    val isMemorySubmitted: LiveData<Boolean> = graduationRepo.isCollegeMemorySubmitted

    fun addCollegeMemory(imageFile:File, email:String, college:String, title:String){
        graduationRepo.addCollegeMemory(imageFile, title, email, college)
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
}