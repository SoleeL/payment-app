package com.soleel.paymentapp.feature.salesprocess

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
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

sealed class SalesProcessUiEvent {
    data class TipSelected(val tipTotal: Float?) : SalesProcessUiEvent()
    data class PaymentMethodSelected(val paymentMethodSelected: PaymentMethodEnum) :
        SalesProcessUiEvent()

    data class CashChangeSelected(val cashChangeSelected: Float?) : SalesProcessUiEvent()
    data class CreditInstalmentsSelected(val creditInstalmentsSelected: Int?) :
        SalesProcessUiEvent()

    data class DebitChangeSelected(val debitChangeSelected: Float?) : SalesProcessUiEvent()
}

@HiltViewModel
open class SalesProcessViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val  calculatorTotal: Float = savedStateHandle.get<Float>("calculatorTotal") ?: 0f

    private var _salesProcessUiModel: SalesProcessUiModel by mutableStateOf(
        SalesProcessUiModel(calculatorTotal)
    )

    val salesProcessUiModel: SalesProcessUiModel get() = _salesProcessUiModel

    fun onSalesProcessUiEvent(event: SalesProcessUiEvent) {
        when (event) {
            is SalesProcessUiEvent.TipSelected -> {
                _salesProcessUiModel = salesProcessUiModel.copy(
                    tipTotal = event.tipTotal
                )
            }

            is SalesProcessUiEvent.PaymentMethodSelected -> {
                _salesProcessUiModel = salesProcessUiModel.copy(
                    paymentMethodSelected = event.paymentMethodSelected
                )
            }

            is SalesProcessUiEvent.CashChangeSelected -> {
                _salesProcessUiModel = salesProcessUiModel.copy(
                    cashChangeSelected = event.cashChangeSelected
                )
            }

            is SalesProcessUiEvent.CreditInstalmentsSelected -> {
                _salesProcessUiModel = salesProcessUiModel.copy(
                    creditInstalmentsSelected = event.creditInstalmentsSelected
                )
            }

            is SalesProcessUiEvent.DebitChangeSelected -> {
                _salesProcessUiModel = salesProcessUiModel.copy(
                    debitChangeSelected = event.debitChangeSelected
                )
            }
        }
    }
}