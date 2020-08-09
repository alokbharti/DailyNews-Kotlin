package com.alok.dailynews.database

import androidx.room.*
import com.alok.dailynews.models.LikedNewsItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDatabaseDao {

    @Insert
    fun insert(newsItem: LikedNewsItem)

    @Delete
    fun delete(newsItem: LikedNewsItem)

    @Query("SELECT * FROM news_article_liked_table ORDER BY newsId DESC")
    fun getAllLikedNewsItem(): Flow<List<LikedNewsItem>>

    @Query("SELECT * FROM news_article_liked_table WHERE newsId = :id")
    fun getFirstNewsArticle(id: Long): LikedNewsItem?

    @Query("SELECT * FROM news_article_liked_table WHERE newsTitle = :title")
    fun getNewsArticleWithTitle(title: String): LikedNewsItem?
}