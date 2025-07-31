package com.soleel.paymentapp.feature.salesprocess.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//data class SetupUiModel(
//    val calculatorTotal: Float,
//
//    val tipTotal: Float? = null,
//    val paymentMethodSelected: PaymentMethodEnum? = null,
//    val cashChangeSelected: Float? = null,
//    val creditInstalmentsSelected: Int? = null,
//    val debitChangeSelected: Float? = null
//)
//
//sealed class SetupUiEvent {
//    data class TipSelected(val tipTotal: Float) : SetupUiEvent()
//    data class PaymentMethodSelected(
//        val paymentMethodSelected: PaymentMethodEnum,
//        val navigationToNextScreen: () -> Unit
//    ) : SetupUiEvent()
//
//    data class CashChangeSelected(val cashChangeSelected: Float) : SetupUiEvent()
//
//    data class CreditInstalmentsSelected(
//        val creditInstalmentsSelected: Int?,
//        val navigationToNextScreen: (sale: Sale) -> Unit
//    ) : SetupUiEvent()
//
//    data class DebitChangeSelected(val debitChangeSelected: Float) : SetupUiEvent()
//}
//
//@HiltViewModel
//open class SetupViewModel @Inject constructor(
//    private val savedStateHandle: SavedStateHandle
//) : ViewModel() {
//    private val calculatorTotal: Float = savedStateHandle.get<Float>("calculatorTotal") ?: 0f
//
//    private var _setupUiModel: SetupUiModel by mutableStateOf(
//        SetupUiModel(calculatorTotal)
//    )
//
//    val setupUiModel: SetupUiModel get() = _setupUiModel
//
//    fun onSetupUiEvent(event: SetupUiEvent) {
//        when (event) {
//            is SetupUiEvent.TipSelected -> TODO()
//
//            is SetupUiEvent.PaymentMethodSelected -> {
//                _setupUiModel = setupUiModel.copy(
//                    paymentMethodSelected = event.paymentMethodSelected
//                )
//                event.navigationToNextScreen()
//            }
//
//            is SetupUiEvent.CashChangeSelected -> TODO()
//
//            is SetupUiEvent.CreditInstalmentsSelected -> {
//                _setupUiModel = setupUiModel.copy(
//                    creditInstalmentsSelected = event.creditInstalmentsSelected
//                )
//                event.navigationToNextScreen(_setupUiModel.toSale())
//            }
//
//            is SetupUiEvent.DebitChangeSelected -> TODO()
//        }
//    }
//
//    private fun SetupUiModel.toSale(): Sale {
//        return Sale(
//            calculatorTotal = calculatorTotal,
//            tipTotal = tipTotal,
//            paymentMethodSelected = paymentMethodSelected,
//            cashChangeSelected = cashChangeSelected,
//            creditInstalmentsSelected = creditInstalmentsSelected,
//            debitChangeSelected = debitChangeSelected
//        )
//    }
//}