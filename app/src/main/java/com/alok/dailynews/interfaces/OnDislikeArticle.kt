package com.alok.dailynews.interfaces

import com.alok.dailynews.models.LikedNewsItem

interface OnDislikeArticle {
    fun onDislike(newsItem: LikedNewsItem)
}