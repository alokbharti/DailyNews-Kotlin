package com.alok.dailynews.interfaces

import com.alok.dailynews.models.NewsItem

interface onSwipeRight {
    fun onSwipeRight(newsItem: NewsItem)
    fun onSwipeLeft(newsItem: NewsItem)
}