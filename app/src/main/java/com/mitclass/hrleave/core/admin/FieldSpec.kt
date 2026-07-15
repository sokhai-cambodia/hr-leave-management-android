package com.mitclass.hrleave.core.admin

enum class FieldType { TEXT, MULTILINE_TEXT, INTEGER, DECIMAL, TOGGLE, DATE, PICKER, PASSWORD }

data class PickerOption(val id: String, val label: String)

/**
 * Describes one form field generically enough to drive both rendering and value
 * conversion for any admin resource — the per-resource "adapter" supplies a list of
 * these plus the read/write glue; the generic list/form Composables never change
 * (Task 10.1's scaffold, reused unmodified by every resource in Tasks 10.2-10.4).
 */
data class FieldSpec(
    val key: String,
    val label: String,
    val type: FieldType,
    val required: Boolean = true,
    val loadPickerOptions: (suspend () -> List<PickerOption>)? = null,
)
