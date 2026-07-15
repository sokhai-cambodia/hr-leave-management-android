package com.mitclass.hrleave.core.network

import com.mitclass.hrleave.data.remote.api.ApprovalsApi
import com.mitclass.hrleave.data.remote.api.AuthApi
import com.mitclass.hrleave.data.remote.api.LeaveBalancesApi
import com.mitclass.hrleave.data.remote.api.LeavePlanRequestsApi
import com.mitclass.hrleave.data.remote.api.LeaveRequestsApi
import com.mitclass.hrleave.data.remote.api.LeaveTypesApi
import com.mitclass.hrleave.data.remote.api.NotificationsApi
import com.mitclass.hrleave.data.remote.api.PoliciesApi
import com.mitclass.hrleave.data.remote.api.PublicHolidaysApi
import com.mitclass.hrleave.data.remote.api.RecommendsApi
import com.mitclass.hrleave.data.remote.api.ScheduleApi
import com.mitclass.hrleave.data.remote.api.TeamsApi
import com.mitclass.hrleave.data.remote.api.UsersApi
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
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideTeamsApi(retrofit: Retrofit): TeamsApi = retrofit.create(TeamsApi::class.java)

    @Provides
    @Singleton
    fun provideApprovalsApi(retrofit: Retrofit): ApprovalsApi = retrofit.create(ApprovalsApi::class.java)

    @Provides
    @Singleton
    fun provideLeaveBalancesApi(retrofit: Retrofit): LeaveBalancesApi =
        retrofit.create(LeaveBalancesApi::class.java)

    @Provides
    @Singleton
    fun provideLeaveRequestsApi(retrofit: Retrofit): LeaveRequestsApi =
        retrofit.create(LeaveRequestsApi::class.java)

    @Provides
    @Singleton
    fun provideLeaveTypesApi(retrofit: Retrofit): LeaveTypesApi =
        retrofit.create(LeaveTypesApi::class.java)

    @Provides
    @Singleton
    fun provideLeavePlanRequestsApi(retrofit: Retrofit): LeavePlanRequestsApi =
        retrofit.create(LeavePlanRequestsApi::class.java)

    @Provides
    @Singleton
    fun provideRecommendsApi(retrofit: Retrofit): RecommendsApi =
        retrofit.create(RecommendsApi::class.java)

    @Provides
    @Singleton
    fun provideScheduleApi(retrofit: Retrofit): ScheduleApi = retrofit.create(ScheduleApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationsApi(retrofit: Retrofit): NotificationsApi =
        retrofit.create(NotificationsApi::class.java)

    @Provides
    @Singleton
    fun providePublicHolidaysApi(retrofit: Retrofit): PublicHolidaysApi =
        retrofit.create(PublicHolidaysApi::class.java)

    @Provides
    @Singleton
    fun providePoliciesApi(retrofit: Retrofit): PoliciesApi = retrofit.create(PoliciesApi::class.java)

    @Provides
    @Singleton
    fun provideUsersApi(retrofit: Retrofit): UsersApi = retrofit.create(UsersApi::class.java)
}
