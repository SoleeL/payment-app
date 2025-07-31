package com.soleel.paymentapp.data.preferences.developer

import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DeveloperPreferencesMock : IDeveloperPreferences {

    private val _preferences = MutableStateFlow(
        mapOf(
            DeveloperPreferenceEnum.CONTACTLESS_READER_INVALID_CARD to true,
            DeveloperPreferenceEnum.CONTACTLESS_READER_FALLBACK to false,
            DeveloperPreferenceEnum.CONTACTLESS_READER_OTHER_ERROR to false
        )
    )

    override val allPreferencesState: Flow<Map<DeveloperPreferenceEnum, Boolean>>
        get() = _preferences

    override suspend fun isEnabled(key: DeveloperPreferenceEnum): Boolean {
        return _preferences.value[key] ?: key.defaultIsEnabled
    }

    override suspend fun setEnabled(key: DeveloperPreferenceEnum, enabled: Boolean) {
        _preferences.update { current ->
            current.toMutableMap().apply { this[key] = enabled }
        }
    }
}