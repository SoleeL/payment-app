package com.soleel.paymentapp.data.preferences.developer

import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DeveloperPreferencesMock : IDeveloperPreferences {

    private val _preferences = MutableStateFlow(
        mapOf(
            DeveloperPreferenceKey.CONTACTLESS_READER_INVALID_CARD to true,
            DeveloperPreferenceKey.CONTACTLESS_READER_FALLBACK to false,
            DeveloperPreferenceKey.CONTACTLESS_READER_OTHER_ERROR to false
        )
    )

    override val allPreferencesState: Flow<Map<DeveloperPreferenceKey, Boolean>>
        get() = _preferences

    override suspend fun isEnabled(key: DeveloperPreferenceKey): Boolean {
        return _preferences.value[key] ?: key.defaultIsEnabled
    }

    override suspend fun setEnabled(key: DeveloperPreferenceKey, enabled: Boolean) {
        _preferences.update { current ->
            current.toMutableMap().apply { this[key] = enabled }
        }
    }
}