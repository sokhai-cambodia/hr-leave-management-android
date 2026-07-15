package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.DangerColor
import com.mitclass.hrleave.core.theme.LightBorder
import com.mitclass.hrleave.core.theme.LightFieldFill
import com.mitclass.hrleave.core.theme.TextFieldCornerRadius

/**
 * Bold label above a filled field (border only shown focused/error), replacing
 * [OutlinedTextField]'s built-in floating label — the Flutter client's convention.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(AppSpacing.sm))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder?.let { { Text(it) } },
            isError = isError,
            supportingText = supportingText?.let { { Text(it) } },
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(TextFieldCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LightFieldFill,
                unfocusedContainerColor = LightFieldFill,
                disabledContainerColor = LightFieldFill,
                errorContainerColor = LightFieldFill,
                // STYLE_GUIDE.md: "outlined/bordered (not borderless), 1.5dp primary-colored
                // border on focus" — a visible border at rest too, not just on focus.
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = LightBorder,
                disabledBorderColor = LightBorder,
                errorBorderColor = DangerColor,
                // STYLE_GUIDE.md: "hint text at 35% opacity."
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
            ),
        )
    }
}
