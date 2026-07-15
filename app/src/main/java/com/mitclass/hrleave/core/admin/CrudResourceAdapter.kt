package com.mitclass.hrleave.core.admin

import com.mitclass.hrleave.core.network.AppResult

/** One implementation per admin resource; everything generic (list/search/form) is driven from this. */
interface CrudResourceAdapter<T> {
    val fields: List<FieldSpec>

    fun id(item: T): String
    fun title(item: T): String
    fun subtitle(item: T): String
    fun toFormValues(item: T): Map<String, String>

    suspend fun list(skip: Int, limit: Int): AppResult<Pair<List<T>, Int>>
    suspend fun create(values: Map<String, String>): AppResult<T>
    suspend fun update(id: String, values: Map<String, String>): AppResult<T>
    suspend fun delete(id: String): AppResult<Unit>
}
