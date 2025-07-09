package com.example.voglioisoldi.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {

    }

}