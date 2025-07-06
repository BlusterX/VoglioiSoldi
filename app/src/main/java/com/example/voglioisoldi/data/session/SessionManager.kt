package com.example.voglioisoldi.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "session_prefs")

//Da spostare in un altro package????
class SessionManager(private val context: Context) {
    companion object {
        private val KEY_LOGGED_USER = stringPreferencesKey("logged_user")
    }

    suspend fun saveLoggedInUser(username: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LOGGED_USER] = username
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_LOGGED_USER)
        }
    }

    fun getLoggedInUser(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_LOGGED_USER]
        }
    }
}