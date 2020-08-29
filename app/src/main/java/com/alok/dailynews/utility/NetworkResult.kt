package com.alok.dailynews.utility

sealed class NetworkResult<out R> {

    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Loading<String>(val message: String) : NetworkResult<String>()

    data class Error<String>(val message: String) : NetworkResult<String>()
}