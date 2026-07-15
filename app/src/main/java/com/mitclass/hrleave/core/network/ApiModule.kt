package com.mitclass.hrleave.core.network

import com.mitclass.hrleave.data.remote.api.HealthCheckApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideHealthCheckApi(retrofit: Retrofit): HealthCheckApi =
        retrofit.create(HealthCheckApi::class.java)
}
