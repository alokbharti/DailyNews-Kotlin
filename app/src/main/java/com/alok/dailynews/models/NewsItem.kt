package com.alok.dailynews.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "news_article_liked_table")
data class NewsItem(
    @PrimaryKey(autoGenerate = true)
    var newsId: Long = 0L,
    @ColumnInfo(name = "read_time_milli")
    val readTimeMilli: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "newsTitle")
    val title:String,
    @ColumnInfo(name = "news_desc")
    val description:String,
    @ColumnInfo(name = "news_image_url")
    val imageUrl:String,
    @ColumnInfo(name = "news_url")
    val newsUrl:String,
    @ColumnInfo(name = "news_is_bookmarked")
    val isBookmarked:Boolean)