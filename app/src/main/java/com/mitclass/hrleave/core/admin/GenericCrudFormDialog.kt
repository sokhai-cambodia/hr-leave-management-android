package com.mitclass.hrleave.core.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.ui.AppTextField
import com.mitclass.hrleave.core.ui.DatePickerField
import com.mitclass.hrleave.core.ui.ErrorBanner
import java.time.LocalDate

@Composable
fun <T> GenericCrudFormDialog(engine: CrudEngine<T>) {
    val mode by engine.formMode.collectAsState()
    if (mode is CrudFormMode.Hidden) return

    val values by engine.formValues.collectAsState()
    val saving by engine.formSaving.collectAsState()
    val error by engine.formError.collectAsState()
    val isEdit = mode is CrudFormMode.Edit

    AlertDialog(
        onDismissRequest = { if (!saving) engine.dismissForm() },
        title = { Text(if (isEdit) "Edit" else "Create") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                engine.adapter.fields.forEach { field ->
                    FieldEditor(
                        field = field,
                        value = values[field.key].orEmpty(),
                        onValueChange = { engine.onFieldChange(field.key, it) },
                    )
                    Spacer(Modifier.height(AppSpacing.md))
                }
                error?.let { ErrorBanner(message = it) }
            }
        },
        confirmButton = {
            Button(onClick = engine::submitForm, enabled = !saving) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = engine::dismissForm, enabled = !saving) { Text("Cancel") }
        },
    )
}

@Composable
private fun FieldEditor(field: FieldSpec, value: String, onValueChange: (String) -> Unit) {
    when (field.type) {
        FieldType.TEXT -> AppTextField(
            value = value,
            onValueChange = onValueChange,
            label = field.label,
            singleLine = true,
        )

        FieldType.MULTILINE_TEXT -> AppTextField(
            value = value,
            onValueChange = onValueChange,
            label = field.label,
            singleLine = false,
        )

        FieldType.INTEGER -> AppTextField(
            value = value,
            onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null || it == "-") onValueChange(it) },
            label = field.label,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        FieldType.DECIMAL -> AppTextField(
            value = value,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".")) onValueChange(it) },
            label = field.label,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        FieldType.PASSWORD -> {
            var visible by remember { mutableStateOf(false) }
            AppTextField(
                value = value,
                onValueChange = onValueChange,
                label = field.label,
                singleLine = true,
                visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { visible = !visible }) {
                        Icon(
                            imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (visible) "Hide" else "Show",
                        )
                    }
                },
            )
        }

        FieldType.TOGGLE -> Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = field.label, modifier = Modifier.weight(1f))
            Switch(checked = value.toBoolean(), onCheckedChange = { onValueChange(it.toString()) })
        }

        FieldType.DATE -> DatePickerField(
            label = field.label,
            date = value.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            onDateSelected = { onValueChange(it.toString()) },
        )

        FieldType.PICKER -> {
            val loadOptions = field.loadPickerOptions
            var options by remember(field.key) { mutableStateOf<List<PickerOption>>(emptyList()) }
            var loaded by remember(field.key) { mutableStateOf(false) }
            LaunchedEffect(field.key) {
                options = loadOptions?.invoke().orEmpty()
                loaded = true
            }
            val selectedLabel = options.firstOrNull { it.id == value }?.label.orEmpty()
            SearchablePickerField(
                label = field.label,
                selectedLabel = selectedLabel,
                options = options,
                loading = !loaded,
                onSelected = { onValueChange(it.id) },
            )
        }
    }
}
