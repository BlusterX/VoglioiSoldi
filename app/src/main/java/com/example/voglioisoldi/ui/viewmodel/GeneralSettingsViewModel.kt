package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.models.ThemeMode
import com.example.voglioisoldi.data.repositories.SettingsRepository
import com.example.voglioisoldi.data.session.SessionManager
import com.example.voglioisoldi.ui.util.BiometricStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GeneralSettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val biometricEnabled: Boolean = false,
    val biometricStatus: BiometricStatus = BiometricStatus.NOT_AVAILABLE,
    val errorMessage: String = ""
)

interface GeneralSettingsActions {
    fun loadSettings()
    fun updateTheme(themeMode: ThemeMode)
    fun setBiometricStatus(status: BiometricStatus)
    fun updateBiometricEnabled(enabled: Boolean)
    fun clearError()
}

class GeneralSettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(GeneralSettingsState())
    val state: StateFlow<GeneralSettingsState> = _state.asStateFlow()

    val actions = object : GeneralSettingsActions {
        override fun loadSettings() {
            viewModelScope.launch {
                try {
                    val themeMode = settingsRepository.getThemeMode().first()
                    val currentUser = sessionManager.getLoggedInUser().first()
                    val biometricUser = settingsRepository.getBiometricUsername().first()

                    _state.update {
                        it.copy(
                            themeMode = themeMode,
                            biometricEnabled = currentUser == biometricUser
                        )
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
                        it.copy(errorMessage = "Errore nel salvataggio del tema: ${e.message}.")
                    }
                }
            }
        }

        override fun setBiometricStatus(status: BiometricStatus) {
            _state.update { it.copy(biometricStatus = status) }
        }

        override fun updateBiometricEnabled(enabled: Boolean) {
            viewModelScope.launch {
                try {
                    if (enabled) {
                        val currentUser = sessionManager.getLoggedInUser().first()
                        settingsRepository.setBiometricUsername(currentUser)
                    } else {
                        settingsRepository.setBiometricUsername(null)
                    }
                    _state.update { it.copy(biometricEnabled = enabled) }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(errorMessage = "Errore nel salvataggio delle impostazioni biometriche: ${e.message}.")
                    }
                }
            }
        }

        override fun clearError() {
            _state.update { it.copy(errorMessage = "") }
        }
    }
}