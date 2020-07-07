package com.alok.dailynews.models


data class NewsItem(
    var title:String,
    var description:String,
    var imageUrl:String,
    var newsUrl:String,
    var newsSourceName:String,
    var isBookmarked:Boolean)