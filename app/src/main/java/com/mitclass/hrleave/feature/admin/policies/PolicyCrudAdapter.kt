package com.mitclass.hrleave.feature.admin.policies

import com.mitclass.hrleave.core.admin.CrudResourceAdapter
import com.mitclass.hrleave.core.admin.FieldSpec
import com.mitclass.hrleave.core.admin.FieldType
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.PolicyDto
import com.mitclass.hrleave.data.remote.dto.PolicyUpsertDto
import com.mitclass.hrleave.data.repository.PoliciesRepository

/** `operation`/`value` accept arbitrary strings — no client-side enum validation, per the backend's own contract. */
class PolicyCrudAdapter(
    private val repository: PoliciesRepository,
) : CrudResourceAdapter<PolicyDto> {

    override val fields: List<FieldSpec> = listOf(
        FieldSpec("code", "Code", FieldType.TEXT),
        FieldSpec("name", "Name", FieldType.TEXT),
        FieldSpec("operation", "Operation", FieldType.TEXT),
        FieldSpec("value", "Value", FieldType.TEXT),
        FieldSpec("score", "Score", FieldType.DECIMAL),
        FieldSpec("description", "Description", FieldType.MULTILINE_TEXT, required = false),
        FieldSpec("is_active", "Active", FieldType.TOGGLE),
    )

    override fun id(item: PolicyDto) = item.id
    override fun title(item: PolicyDto) = item.name
    override fun subtitle(item: PolicyDto) = "${item.code} ${item.operation.orEmpty()} ${item.value}"

    override fun toFormValues(item: PolicyDto) = mapOf(
        "code" to item.code,
        "name" to item.name,
        "operation" to item.operation.orEmpty(),
        "value" to item.value,
        "score" to (item.score ?: 0.0).toString(),
        "description" to item.description.orEmpty(),
        "is_active" to item.isActive.toString(),
    )

    override suspend fun list(skip: Int, limit: Int): AppResult<Pair<List<PolicyDto>, Int>> =
        repository.listAll(skip, limit)

    override suspend fun create(values: Map<String, String>): AppResult<PolicyDto> =
        repository.create(toUpsertDto(values))

    override suspend fun update(id: String, values: Map<String, String>): AppResult<PolicyDto> =
        repository.update(id, toUpsertDto(values))

    override suspend fun delete(id: String): AppResult<Unit> = repository.delete(id)

    private fun toUpsertDto(values: Map<String, String>) = PolicyUpsertDto(
        code = values["code"].orEmpty(),
        name = values["name"].orEmpty(),
        operation = values["operation"].orEmpty(),
        value = values["value"].orEmpty(),
        score = values["score"]?.toDoubleOrNull() ?: 0.0,
        description = values["description"]?.ifBlank { null },
        isActive = values["is_active"]?.toBoolean() ?: true,
    )
}
