package com.alok.dailynews.api.responsemodel

import com.google.gson.annotations.SerializedName

data class CollegeListResponse(
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String
)