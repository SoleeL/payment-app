package com.soleel.paymentapp.feature.salesprocess.setup.creditinstalmentsselection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


sealed class InstalmentUiEvent {
    data class WhenIsNumber(val value: Int) : InstalmentUiEvent()
    data class WhenIsOther(val value: String) : InstalmentUiEvent()
    data object WhenIsWithout : InstalmentUiEvent()
}

@HiltViewModel
open class CreditInstalmentsSelectionViewModel @Inject constructor() : ViewModel() {

    private var _instalmentsUiEvent: List<InstalmentUiEvent> by mutableStateOf(
        listOf(
            InstalmentUiEvent.WhenIsNumber(3),
            InstalmentUiEvent.WhenIsNumber(6),
            InstalmentUiEvent.WhenIsNumber(9),
            InstalmentUiEvent.WhenIsNumber(12),
            InstalmentUiEvent.WhenIsOther("Otra cantidad"),
            InstalmentUiEvent.WhenIsWithout
        )
    )

    val instalmentsUiEvent: List<InstalmentUiEvent> get() = _instalmentsUiEvent

    fun onInstalmentUiEvent(event: InstalmentUiEvent, setupEvent: () -> Unit) {
        when (event) {
            is InstalmentUiEvent.WhenIsNumber -> setupEvent()
            is InstalmentUiEvent.WhenIsOther -> TODO()
            is InstalmentUiEvent.WhenIsWithout -> setupEvent()
        }
    }
}