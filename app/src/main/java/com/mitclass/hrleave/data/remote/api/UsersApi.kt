package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.MessageDto
import com.mitclass.hrleave.data.remote.dto.UserCreateUpsertDto
import com.mitclass.hrleave.data.remote.dto.UserDto
import com.mitclass.hrleave.data.remote.dto.UserUpdateUpsertDto
import com.mitclass.hrleave.data.remote.dto.UsersResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersApi {
    @GET("users/")
    suspend fun list(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): UsersResponseDto

    @POST("users/")
    suspend fun create(@Body body: UserCreateUpsertDto): UserDto

    @PATCH("users/{id}")
    suspend fun update(@Path("id") id: String, @Body body: UserUpdateUpsertDto): UserDto

    @DELETE("users/{id}")
    suspend fun delete(@Path("id") id: String): MessageDto
}
