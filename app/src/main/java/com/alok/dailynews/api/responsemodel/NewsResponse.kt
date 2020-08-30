package com.alok.dailynews.api.responsemodel

import com.google.gson.annotations.SerializedName

class NewsResponse {

    @SerializedName("status")
    val status: String? = null

    @SerializedName("articles")
    var newsData: List<NewsArticle>? = null

    data class Source(
        @SerializedName("id")
        var id: String,
        @SerializedName("name")
        var name: String
    )

    data class NewsArticle(
        @SerializedName("source")
        var newsSourceName:Source,
        @SerializedName("author")
        var author: String,
        @SerializedName("title")
        var title:String,
        @SerializedName("description")
        var description:String,
        @SerializedName("url")
        var newsUrl:String,
        @SerializedName("urlToImage")
        var imageUrl:String,
        @SerializedName("publishedAt")
        var publishedAt: String,
        @SerializedName("content")
        var content: String)
}