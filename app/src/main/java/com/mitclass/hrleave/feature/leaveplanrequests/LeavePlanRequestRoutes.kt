package com.mitclass.hrleave.feature.leaveplanrequests

import java.time.LocalDate

object LeavePlanRequestRoutes {
    const val DETAIL_ARG = "id"
    const val DETAIL_ROUTE = "leave_plan_request_detail/{$DETAIL_ARG}"
    fun detail(id: String) = "leave_plan_request_detail/$id"

    const val FORM_ARG = "id"
    const val PREFILL_LEAVE_TYPE_ARG = "leaveTypeId"
    const val PREFILL_DATES_ARG = "dates"
    const val FORM_ROUTE =
        "leave_plan_request_form?id={$FORM_ARG}&leaveTypeId={$PREFILL_LEAVE_TYPE_ARG}&dates={$PREFILL_DATES_ARG}"
    const val FORM_CREATE_ROUTE = "leave_plan_request_form"
    fun formEdit(id: String) = "leave_plan_request_form?id=$id"

    /** Task 6.2: land on the create form pre-populated from a recommendation selection. */
    fun formPrefill(leaveTypeId: String, dates: List<LocalDate>) =
        "leave_plan_request_form?leaveTypeId=$leaveTypeId&dates=${dates.joinToString(",")}"
}
