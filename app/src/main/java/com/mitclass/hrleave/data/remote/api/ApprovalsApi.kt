package com.mitclass.hrleave.data.remote.api

import com.mitclass.hrleave.data.remote.dto.PendingApprovalsCountDto
import retrofit2.http.GET

interface ApprovalsApi {
    @GET("approvals/pending-count")
    suspend fun pendingCount(): PendingApprovalsCountDto
}
