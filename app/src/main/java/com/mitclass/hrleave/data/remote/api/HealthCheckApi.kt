package com.mitclass.hrleave.data.remote.api

import retrofit2.http.GET

interface HealthCheckApi {
    @GET("utils/health-check/")
    suspend fun check(): Boolean
}
