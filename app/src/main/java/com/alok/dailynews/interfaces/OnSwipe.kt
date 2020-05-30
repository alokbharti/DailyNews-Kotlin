package com.alok.dailynews.interfaces

import com.alok.dailynews.models.NewsItem

interface OnSwipe {
    fun onSwipeRight(newsItem: NewsItem)
    fun onSwipeLeft(newsItem: NewsItem)
}