package com.example.customdownloadmanger.data

sealed class IResult<out T : Any> {

    data class Success<out T : Any>(val data: T? = null) : IResult<T>()

    data class Error(val throwable: Throwable) : IResult<Nothing>()

    object Loading : IResult<Nothing>()

}