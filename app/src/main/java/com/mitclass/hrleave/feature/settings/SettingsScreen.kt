package com.mitclass.hrleave.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.BuildConfig
import com.mitclass.hrleave.R
import com.mitclass.hrleave.core.storage.ThemeMode
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.ui.PillTabRow

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(AppSpacing.lg)) {
        SectionTitle(stringResource(R.string.settings_appearance_section_title))
        Spacer(Modifier.height(AppSpacing.sm))
        val themeOptions = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)
        PillTabRow(
            titles = listOf(
                stringResource(R.string.settings_theme_system),
                stringResource(R.string.settings_theme_light),
                stringResource(R.string.settings_theme_dark),
            ),
            selectedIndex = themeOptions.indexOf(state.themeMode).coerceAtLeast(0),
            onSelect = { index -> viewModel.onThemeModeSelected(themeOptions[index]) },
        )

        Spacer(Modifier.height(AppSpacing.xl))
        HorizontalDivider()
        Spacer(Modifier.height(AppSpacing.lg))

        SectionTitle(stringResource(R.string.settings_notifications_section_title))
        Spacer(Modifier.height(AppSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_notifications_toggle_label),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.settings_notifications_toggle_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = state.notificationsEnabled, onCheckedChange = viewModel::onNotificationsToggled)
        }

        Spacer(Modifier.height(AppSpacing.xl))
        HorizontalDivider()
        Spacer(Modifier.height(AppSpacing.lg))

        SectionTitle(stringResource(R.string.settings_about_section_title))
        Spacer(Modifier.height(AppSpacing.sm))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.settings_about_version_label),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(text = BuildConfig.VERSION_NAME, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}
