package com.alok.dailynews.utility

sealed class NetworkResult<out T: Any> {

    data class Success(val message: String, val data: Any) : NetworkResult<Any>()

    data class Loading(val message: String, val data: Any?) : NetworkResult<Any>()

    data class Error(val message: String) : NetworkResult<String>()
}