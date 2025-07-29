package com.soleel.paymentapp.feature.salesprocess

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.soleel.paymentapp.core.model.PaymentMethodEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class SalesProcessUiModel(
    val calculatorTotal: Float,

    val tipTotal: Float? = null,
    val paymentMethodSelected: PaymentMethodEnum? = null,
    val cashChangeSelected: Float? = null,
    val creditInstalmentsSelected: Int? = null,
    val debitChangeSelected: Float? = null
)


@HiltViewModel
open class SalesProcessViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val calculatorTotal: Int = savedStateHandle.get<Int>("calculatorTotal") ?: 0

    private var _salesProcessUiModel: SalesProcessUiModel by mutableStateOf(
        SalesProcessUiModel(calculatorTotal)
    )

    val salesProcessUiModel: SalesProcessUiModel get() = _salesProcessUiModel
}