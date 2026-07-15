package com.mitclass.hrleave.data.repository

import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.core.network.safeApiCall
import com.mitclass.hrleave.data.remote.api.ApprovalsApi
import com.mitclass.hrleave.data.remote.dto.PendingApprovalsCountDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApprovalsRepository @Inject constructor(
    private val approvalsApi: ApprovalsApi,
) {
    suspend fun pendingCount(): AppResult<PendingApprovalsCountDto> =
        safeApiCall { approvalsApi.pendingCount() }
}
