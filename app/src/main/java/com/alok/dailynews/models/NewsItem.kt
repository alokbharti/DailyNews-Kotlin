package com.alok.dailynews.models


data class NewsItem(
    var title:String,
    var description:String? = null,
    var imageUrl:String? = null,
    var newsUrl:String,
    var newsSourceName:String)