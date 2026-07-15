package com.mitclass.hrleave.feature.admin.leavetypes

import com.mitclass.hrleave.core.admin.CrudResourceAdapter
import com.mitclass.hrleave.core.admin.FieldSpec
import com.mitclass.hrleave.core.admin.FieldType
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.LeaveTypeDto
import com.mitclass.hrleave.data.remote.dto.LeaveTypeUpsertDto
import com.mitclass.hrleave.data.repository.LeaveTypesRepository

/** Proves the generic scaffold (Task 10.1) — simplest resource, no relational fields. */
class LeaveTypeCrudAdapter(
    private val repository: LeaveTypesRepository,
) : CrudResourceAdapter<LeaveTypeDto> {

    override val fields: List<FieldSpec> = listOf(
        FieldSpec("code", "Code", FieldType.TEXT),
        FieldSpec("name", "Name", FieldType.TEXT),
        FieldSpec("entitlement", "Entitlement (days)", FieldType.INTEGER),
        FieldSpec("description", "Description", FieldType.MULTILINE_TEXT, required = false),
        FieldSpec("is_allow_plan", "Allow leave planning", FieldType.TOGGLE),
        FieldSpec("is_active", "Active", FieldType.TOGGLE),
    )

    override fun id(item: LeaveTypeDto) = item.id
    override fun title(item: LeaveTypeDto) = item.name
    override fun subtitle(item: LeaveTypeDto) = item.code

    override fun toFormValues(item: LeaveTypeDto) = mapOf(
        "code" to item.code,
        "name" to item.name,
        "entitlement" to item.entitlement.toString(),
        "description" to item.description.orEmpty(),
        "is_allow_plan" to item.isAllowPlan.toString(),
        "is_active" to item.isActive.toString(),
    )

    override suspend fun list(skip: Int, limit: Int): AppResult<Pair<List<LeaveTypeDto>, Int>> =
        repository.listAll(skip, limit)

    override suspend fun create(values: Map<String, String>): AppResult<LeaveTypeDto> =
        repository.create(toUpsertDto(values))

    override suspend fun update(id: String, values: Map<String, String>): AppResult<LeaveTypeDto> =
        repository.update(id, toUpsertDto(values))

    override suspend fun delete(id: String): AppResult<Unit> = repository.delete(id)

    private fun toUpsertDto(values: Map<String, String>) = LeaveTypeUpsertDto(
        code = values["code"].orEmpty(),
        name = values["name"].orEmpty(),
        entitlement = values["entitlement"]?.toIntOrNull() ?: 0,
        description = values["description"]?.ifBlank { null },
        isAllowPlan = values["is_allow_plan"]?.toBoolean() ?: true,
        isActive = values["is_active"]?.toBoolean() ?: true,
    )
}
