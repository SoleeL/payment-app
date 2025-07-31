package com.soleel.paymentapp.data.preferences.developer

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeveloperPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : IDeveloperPreferences {

    override val allPreferencesState: Flow<Map<DeveloperPreferenceKey, Boolean>> =
        dataStore.data.map { preferences ->
            DeveloperPreferenceKey.entries.associateWith { key ->
                val prefKey = booleanPreferencesKey(key.key)
                preferences[prefKey] ?: key.defaultIsEnabled
            }
        }

    override suspend fun isEnabled(key: DeveloperPreferenceKey): Boolean {
        val preferences = dataStore.data.first()
        val booleanKey = booleanPreferencesKey(key.key)
        return preferences[booleanKey] == true
    }

    override suspend fun setEnabled(key: DeveloperPreferenceKey, enabled: Boolean) {
        val booleanKey = booleanPreferencesKey(key.key)
        dataStore.edit { preferences ->
            preferences[booleanKey] = enabled
        }
    }
}