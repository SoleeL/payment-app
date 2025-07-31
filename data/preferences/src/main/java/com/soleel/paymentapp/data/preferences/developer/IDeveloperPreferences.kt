package com.soleel.paymentapp.data.preferences.developer

import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceEnum
import kotlinx.coroutines.flow.Flow

interface IDeveloperPreferences {
    val allPreferencesState: Flow<Map<DeveloperPreferenceEnum, Boolean>>
    suspend fun isEnabled(key: DeveloperPreferenceEnum): Boolean
    suspend fun setEnabled(key: DeveloperPreferenceEnum, enabled: Boolean)
}