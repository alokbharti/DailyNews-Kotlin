package com.alok.dailynews.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "news_article_liked_table")
data class LikedNewsItem(
    @PrimaryKey(autoGenerate = true)
    var newsId: Long = 0L,
    @ColumnInfo(name = "read_time_milli")
    val readTimeMilli: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "newsTitle")
    var title:String = "Demo Title",
    @ColumnInfo(name = "news_desc")
    var description:String = "Demo desc",
    @ColumnInfo(name = "news_image_url")
    var imageUrl:String = "Demo Image url",
    @ColumnInfo(name = "news_url")
    var newsUrl:String = "Demo news url",
    @ColumnInfo(name = "is_news_bookmarked")
    var isBookmarked:Boolean = true
)