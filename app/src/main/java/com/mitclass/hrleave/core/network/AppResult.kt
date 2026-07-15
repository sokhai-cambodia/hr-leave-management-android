package com.mitclass.hrleave.core.network

import com.mitclass.hrleave.core.errors.ApiError
import retrofit2.HttpException
import java.io.IOException

/** Standard success/failure wrapper for repository calls, feeding loading/error UI state. */
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val message: String, val httpCode: Int? = null) : AppResult<Nothing>()
}

suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: IOException) {
    AppResult.Failure(ApiError.NETWORK_ERROR_MESSAGE)
} catch (e: HttpException) {
    AppResult.Failure(ApiError.parse(e.response()?.errorBody()?.string()), e.code())
}
