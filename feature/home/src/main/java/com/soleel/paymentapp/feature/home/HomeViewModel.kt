package com.soleel.paymentapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceEnum
import com.soleel.paymentapp.data.preferences.developer.IDeveloperPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class DeveloperToggleUiState(
    val developerPreferenceKey: DeveloperPreferenceEnum,
    var isEnabled: Boolean
)

sealed class DeveloperToggleUiEvent {
    data class ReverseDeveloperPreference(val developerToggleUiState: DeveloperToggleUiState) :
        DeveloperToggleUiEvent()
}

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val developerPreferences: IDeveloperPreferences
) : ViewModel() {

    private var _developerTogglesUiState: Flow<List<DeveloperToggleUiState>> = developerPreferences
        .allPreferencesState
        .map(transform = { getDeveloperTogglesData(it) })

    private fun getDeveloperTogglesData(developerTogglesDataResult: Map<DeveloperPreferenceEnum, Boolean>): List<DeveloperToggleUiState> {
        return developerTogglesDataResult.map(
            transform = { (key, enabled) ->
                DeveloperToggleUiState(
                    developerPreferenceKey = key,
                    isEnabled = enabled
                )
            }
        )
    }

    val developerTogglesUiState: StateFlow<List<DeveloperToggleUiState>>
        get() = _developerTogglesUiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeveloperPreferenceEnum.entries.map { key ->
                DeveloperToggleUiState(key, key.defaultIsEnabled)
            }
        )

    fun onDeveloperToggleUiEvent(event: DeveloperToggleUiEvent) {
        when (event) {
            is DeveloperToggleUiEvent.ReverseDeveloperPreference -> {
                val key = event.developerToggleUiState.developerPreferenceKey
                val currentState = event.developerToggleUiState.isEnabled

                viewModelScope.launch {
                    developerPreferences.setEnabled(key, !currentState)
                }
            }
        }
    }
}