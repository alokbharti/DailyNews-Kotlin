package com.alok.dailynews.ui.liked

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alok.dailynews.database.NewsDatabaseDao

class LikedNewsViewModelFactory(private val dataSource: NewsDatabaseDao,
                                private val application: Application): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LikedNewsViewModel::class.java)){
            return LikedNewsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}