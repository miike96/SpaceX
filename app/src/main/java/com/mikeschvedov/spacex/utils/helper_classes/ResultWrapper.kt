package com.mikeschvedov.spacex.utils.helper_classes

sealed class ResultWrapper<out T: Any>

//Success
data class Success<out T: Any>(val data: T): ResultWrapper<T>()
//Failure
data class Failure(val exc: Throwable?): ResultWrapper<Nothing>()

