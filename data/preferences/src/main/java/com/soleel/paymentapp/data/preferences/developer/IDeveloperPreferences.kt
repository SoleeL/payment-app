package com.soleel.paymentapp.data.preferences.developer

import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceKey
import kotlinx.coroutines.flow.Flow

interface IDeveloperPreferences {
    val allPreferencesState: Flow<Map<DeveloperPreferenceKey, Boolean>>
    suspend fun isEnabled(key: DeveloperPreferenceKey): Boolean
    suspend fun setEnabled(key: DeveloperPreferenceKey, enabled: Boolean)
}