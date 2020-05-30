package com.alok.dailynews.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.alok.dailynews.models.LikedNewsItem

@Dao
interface NewsDatabaseDao {

    @Insert
    fun insert(newsItem: LikedNewsItem)

    @Delete
    fun delete(newsItem: LikedNewsItem)

    @Query("SELECT * FROM news_article_liked_table ORDER BY newsId DESC")
    fun getAllLikedNewsItem(): LiveData<List<LikedNewsItem>>

    @Query("SELECT * FROM news_article_liked_table WHERE newsId = :id")
    fun getFirstNewsArticle(id: Long): LikedNewsItem?
}