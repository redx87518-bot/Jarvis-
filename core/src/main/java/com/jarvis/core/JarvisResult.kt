package com.jarvis.core

sealed interface JarvisResult<out T> {
    data class Success<T>(val data: T) : JarvisResult<T>
    data class Error(val message: String, val cause: Throwable? = null) : JarvisResult<Nothing>
    data class Loading<T>(val data: T? = null) : JarvisResult<T>

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
}

inline fun <T> JarvisResult<T>.getOrNull(): T? =
    (this as? JarvisResult.Success)?.data

inline fun <T> JarvisResult<T>.getOrElse(default: (JarvisResult.Error) -> T): T =
    when (this) {
        is JarvisResult.Success -> data
        is JarvisResult.Error -> default(this)
        is JarvisResult.Loading -> default(JarvisResult.Error("Loading"))
    }

inline fun <T, R> JarvisResult<T>.map(transform: (T) -> R): JarvisResult<R> =
    when (this) {
        is JarvisResult.Success -> JarvisResult.Success(transform(data))
        is JarvisResult.Error -> this
        is JarvisResult.Loading -> JarvisResult.Loading(data?.let { transform(it) })
    }
