package com.mitclass.hrleave.feature.admin.leavebalances

import com.mitclass.hrleave.core.admin.CrudResourceAdapter
import com.mitclass.hrleave.core.admin.FieldSpec
import com.mitclass.hrleave.core.admin.FieldType
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeaveBalanceDto
import com.mitclass.hrleave.data.remote.dto.LeaveBalanceUpsertDto
import com.mitclass.hrleave.data.repository.LeaveBalancesRepository
import com.mitclass.hrleave.data.repository.LeaveTypesRepository
import com.mitclass.hrleave.data.repository.UsersRepository

/**
 * `owner_id`/`leave_type_id` both use the relational-picker pattern; `year` stays a plain
 * 4-digit-string field (matches the backend's `str`-typed column, not numeric). `taken_balance`/
 * `available_balance` are server-computed and never part of the create/update payload.
 */
class LeaveBalanceCrudAdapter(
    private val leaveBalancesRepository: LeaveBalancesRepository,
    usersRepository: UsersRepository,
    leaveTypesRepository: LeaveTypesRepository,
) : CrudResourceAdapter<LeaveBalanceDto> {

    override val fields: List<FieldSpec> = listOf(
        FieldSpec(
            "owner_id",
            "Employee",
            FieldType.PICKER,
            loadPickerOptions = { usersRepository.listForPicker().let { if (it is AppResult.Success) it.data else emptyList() } },
        ),
        FieldSpec(
            "leave_type_id",
            "Leave type",
            FieldType.PICKER,
            loadPickerOptions = {
                leaveTypesRepository.listForPicker().let { if (it is AppResult.Success) it.data else emptyList() }
            },
        ),
        FieldSpec("year", "Year", FieldType.TEXT),
        FieldSpec("balance", "Balance", FieldType.DECIMAL),
    )

    override fun id(item: LeaveBalanceDto) = item.id
    override fun title(item: LeaveBalanceDto) = item.owner?.fullName ?: item.owner?.email.orEmpty()
    override fun subtitle(item: LeaveBalanceDto) = "${item.leaveType.name} · ${item.year} · ${item.balance}"

    override fun toFormValues(item: LeaveBalanceDto) = mapOf(
        "owner_id" to item.ownerId,
        "leave_type_id" to item.leaveTypeId,
        "year" to item.year,
        "balance" to item.balance.toString(),
    )

    override suspend fun list(skip: Int, limit: Int): AppResult<Pair<List<LeaveBalanceDto>, Int>> =
        leaveBalancesRepository.listAll(skip, limit)

    override suspend fun create(values: Map<String, String>): AppResult<LeaveBalanceDto> =
        leaveBalancesRepository.create(toUpsertDto(values))

    override suspend fun update(id: String, values: Map<String, String>): AppResult<LeaveBalanceDto> =
        leaveBalancesRepository.update(id, toUpsertDto(values))

    override suspend fun delete(id: String): AppResult<Unit> = leaveBalancesRepository.delete(id)

    private fun toUpsertDto(values: Map<String, String>) = LeaveBalanceUpsertDto(
        ownerId = values["owner_id"].orEmpty(),
        leaveTypeId = values["leave_type_id"].orEmpty(),
        year = values["year"].orEmpty(),
        balance = values["balance"]?.toDoubleOrNull() ?: 0.0,
    )
}
