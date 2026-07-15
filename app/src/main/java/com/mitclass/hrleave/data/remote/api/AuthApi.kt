package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.MessageDto
import com.mitclass.hrleave.data.remote.dto.NewPasswordDto
import com.mitclass.hrleave.data.remote.dto.TokenDto
import com.mitclass.hrleave.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {

    @FormUrlEncoded
    @POST("login/access-token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): TokenDto

    @POST("login/test-token")
    suspend fun testToken(): UserDto

    @GET("users/me")
    suspend fun getMe(): UserDto

    @POST("password-recovery/{email}")
    suspend fun recoverPassword(@Path("email") email: String): MessageDto

    @POST("reset-password/")
    suspend fun resetPassword(@Body body: NewPasswordDto): MessageDto
}
