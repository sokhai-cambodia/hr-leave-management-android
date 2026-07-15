package com.mitclass.hrleave.feature.leaverequests

object LeaveRequestRoutes {
    const val DETAIL_ARG = "id"
    const val DETAIL_ROUTE = "leave_request_detail/{$DETAIL_ARG}"

    fun detail(id: String) = "leave_request_detail/$id"
}
