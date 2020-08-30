package com.alok.dailynews.api.responsemodel

import com.google.gson.annotations.SerializedName

data class GraduationItemResponse(
    @SerializedName("title")
    var title: String,
    @SerializedName("image")
    var imageUrl: String
)