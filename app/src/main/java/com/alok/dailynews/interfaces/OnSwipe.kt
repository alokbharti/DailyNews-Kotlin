package com.alok.dailynews.interfaces

import com.alok.dailynews.models.NewsItem

interface OnSwipe <T> {
    fun onSwipeRight(item: T)
    fun onSwipeLeft(item: T)
}