package com.example.voglioisoldi.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.voglioisoldi.data.models.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    fun getThemeMode(): Flow<ThemeMode> {
        return dataStore.data
            .map { preferences ->
                try {
                    ThemeMode.valueOf(preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name)
                } catch (_: Exception) {
                    ThemeMode.SYSTEM
                }
            }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) =
        dataStore.edit { it[THEME_MODE_KEY] = themeMode.name }
}