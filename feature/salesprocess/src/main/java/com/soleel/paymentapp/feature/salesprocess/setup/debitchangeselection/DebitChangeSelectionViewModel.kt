package com.soleel.paymentapp.feature.salesprocess.setup.debitchangeselection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


sealed class ChangeUiEvent {
    data class WhenIsNumber(val value: Int) : ChangeUiEvent()
    data object WhenIsWithout : ChangeUiEvent()
}

@HiltViewModel
open class DebitChangeSelectionViewModel @Inject constructor() : ViewModel() {
    private var _debitChangeUiEvent: List<ChangeUiEvent> by mutableStateOf(
        listOf(
            ChangeUiEvent.WhenIsNumber(5000),
            ChangeUiEvent.WhenIsNumber(10000),
            ChangeUiEvent.WhenIsNumber(15000),
            ChangeUiEvent.WhenIsNumber(20000),
            ChangeUiEvent.WhenIsWithout
        )
    )

    val debitChangeUiEvent: List<ChangeUiEvent> get() = _debitChangeUiEvent
}

