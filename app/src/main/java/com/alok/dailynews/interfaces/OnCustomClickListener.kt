package com.alok.dailynews.interfaces

import com.alok.dailynews.models.LikedNewsItem

interface OnCustomClickListener<T> {
    fun onClick(obj : T)
}