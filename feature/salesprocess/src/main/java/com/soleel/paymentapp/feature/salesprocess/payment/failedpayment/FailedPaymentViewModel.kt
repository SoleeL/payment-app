package com.soleel.paymentapp.feature.salesprocess.payment.failedpayment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.soleel.paymentapp.core.model.paymentprocess.PaymentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
open class FailedPaymentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val paymentResult: PaymentResult = savedStateHandle.get<PaymentResult>("paymentResult") ?: PaymentResult(isSuccess = false)
}