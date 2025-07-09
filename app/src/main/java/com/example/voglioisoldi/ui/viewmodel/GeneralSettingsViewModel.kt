package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.models.ThemeMode
import com.example.voglioisoldi.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GeneralSettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val errorMessage: String = ""
)

interface GeneralSettingsActions {
    fun loadSettings()
    fun updateTheme(themeMode: ThemeMode)
    fun clearError()
}

class GeneralSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(GeneralSettingsState())
    val state: StateFlow<GeneralSettingsState> = _state.asStateFlow()

    val actions = object : GeneralSettingsActions {
        override fun loadSettings() {
            viewModelScope.launch {
                try {
                    settingsRepository.getThemeMode().collect { themeMode: ThemeMode ->
                        _state.update {
                            it.copy(
                                themeMode = themeMode
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = "Errore nel caricamento delle impostazioni: ${e.message}."
                        )
                    }
                }
            }
        }

        override fun updateTheme(themeMode: ThemeMode) {
            viewModelScope.launch {
                try {
                    settingsRepository.setThemeMode(themeMode)
                    _state.update {
                        it.copy(themeMode = themeMode)
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(errorMessage = "Errore nel salvataggio del tema: ${e.message}")
                    }
                }
            }
        }

        override fun clearError() {
            _state.update { it.copy(errorMessage = "") }
        }
    }
}