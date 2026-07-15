package com.mitclass.hrleave.feature.admin.holidays

import com.mitclass.hrleave.core.admin.CrudResourceAdapter
import com.mitclass.hrleave.core.admin.FieldSpec
import com.mitclass.hrleave.core.admin.FieldType
import com.mitclass.hrleave.core.network.AppResult
import com.mitclass.hrleave.data.remote.dto.PublicHolidayDto
import com.mitclass.hrleave.data.remote.dto.PublicHolidayUpsertDto
import com.mitclass.hrleave.data.repository.PublicHolidaysRepository

/** `date` round-trips as a plain "YYYY-MM-DD" string — only the DATE field type's UI treats it as a date. */
class PublicHolidayCrudAdapter(
    private val repository: PublicHolidaysRepository,
) : CrudResourceAdapter<PublicHolidayDto> {

    override val fields: List<FieldSpec> = listOf(
        FieldSpec("date", "Date", FieldType.DATE),
        FieldSpec("name", "Name", FieldType.TEXT),
        FieldSpec("description", "Description", FieldType.MULTILINE_TEXT, required = false),
    )

    override fun id(item: PublicHolidayDto) = item.id
    override fun title(item: PublicHolidayDto) = item.name
    override fun subtitle(item: PublicHolidayDto) = item.date

    override fun toFormValues(item: PublicHolidayDto) = mapOf(
        "date" to item.date,
        "name" to item.name,
        "description" to item.description.orEmpty(),
    )

    override suspend fun list(skip: Int, limit: Int): AppResult<Pair<List<PublicHolidayDto>, Int>> =
        repository.listAll(skip, limit)

    override suspend fun create(values: Map<String, String>): AppResult<PublicHolidayDto> =
        repository.create(toUpsertDto(values))

    override suspend fun update(id: String, values: Map<String, String>): AppResult<PublicHolidayDto> =
        repository.update(id, toUpsertDto(values))

    override suspend fun delete(id: String): AppResult<Unit> = repository.delete(id)

    private fun toUpsertDto(values: Map<String, String>) = PublicHolidayUpsertDto(
        date = values["date"].orEmpty(),
        name = values["name"].orEmpty(),
        description = values["description"]?.ifBlank { null },
    )
}
