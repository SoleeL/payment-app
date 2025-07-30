package com.soleel.paymentapp.feature.salesprocess.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soleel.paymentapp.core.model.Sale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface ReadingUiState {
    data object Reading : ReadingUiState
    data object Success : ReadingUiState
    data object Failure : ReadingUiState
}

@HiltViewModel
open class PaymentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val sale: Sale = savedStateHandle.get<Sale>("sale") ?: Sale(calculatorTotal = 0f)

    // Contactless
    private val _contactlessReadingUiState: Flow<ReadingUiState> = getContactlessReadingUiState()

    private fun getContactlessReadingUiState(): Flow<ReadingUiState> = flow(
        block = {
            emit(ReadingUiState.Reading)
            delay(5_000)
//            emit(ReadingUiState.Success)
            emit(ReadingUiState.Failure)

//      if sharedpreference ->      emit(ReadingUiState.Failure)
        }
    )

    val contactlessReadingUiState: StateFlow<ReadingUiState> = _contactlessReadingUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ReadingUiState.Reading
        )

    // Contact
    private val _contactReadingUiState: Flow<ReadingUiState> = getContactReadingUiState()

    private fun getContactReadingUiState(): Flow<ReadingUiState> = flow(
        block = {
            emit(ReadingUiState.Reading)
            delay(5_000)
            emit(ReadingUiState.Success)

//      if sharedpreference ->      emit(ContactlessReadingUiState.Failure)
        }
    )

    val contactReadingUiState: StateFlow<ReadingUiState> = _contactReadingUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ReadingUiState.Reading
        )

}