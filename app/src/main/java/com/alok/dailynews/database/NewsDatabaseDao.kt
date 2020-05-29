package com.alok.dailynews.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alok.dailynews.models.LikedNewsItem

@Dao
interface NewsDatabaseDao {

    @Insert
    fun insert(newsItem: LikedNewsItem)

    @Update
    fun update(newsItem: LikedNewsItem)

    @Query("SELECT * FROM news_article_liked_table ORDER BY newsId DESC")
    fun getAllLikedNewsItem(): LiveData<List<LikedNewsItem>>

    @Query("SELECT * FROM news_article_liked_table WHERE newsId = :id")
    fun getFirstNewsArticle(id: Long): LikedNewsItem?
}