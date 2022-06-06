package com.mj.pokemonapp.util

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

fun <T> Result<T>.getValue(): T {

    return if(this is Result.Success) {
        this.data
    } else {
        throw (this as Result.Error).exception
    }

}

val Result<*>.succeeded
    get() = this is Result.Success && data != null
