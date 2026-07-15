package com.mitclass.hrleave.feature.leaveplanrequests

object LeavePlanRequestRoutes {
    const val DETAIL_ARG = "id"
    const val DETAIL_ROUTE = "leave_plan_request_detail/{$DETAIL_ARG}"
    fun detail(id: String) = "leave_plan_request_detail/$id"

    const val FORM_ARG = "id"
    const val FORM_ROUTE = "leave_plan_request_form?id={$FORM_ARG}"
    const val FORM_CREATE_ROUTE = "leave_plan_request_form"
    fun formEdit(id: String) = "leave_plan_request_form?id=$id"
}
