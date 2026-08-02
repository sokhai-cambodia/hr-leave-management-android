package com.mitclass.hrleave.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitclass.hrleave.core.storage.ThemeMode
import com.mitclass.hrleave.core.storage.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferencesRepository.themeMode,
        userPreferencesRepository.notificationsEnabled,
    ) { themeMode, notificationsEnabled ->
        SettingsUiState(themeMode = themeMode, notificationsEnabled = notificationsEnabled)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch { userPreferencesRepository.setThemeMode(mode) }
    }

    fun onNotificationsToggled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setNotificationsEnabled(enabled) }
    }
}
