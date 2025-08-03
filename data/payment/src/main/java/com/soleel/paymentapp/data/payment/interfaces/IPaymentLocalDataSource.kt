package com.soleel.paymentapp.data.payment.interfaces

import com.soleel.paymentapp.core.model.base.Payment
import kotlinx.coroutines.flow.Flow

interface IPaymentLocalDataSource {

    suspend fun createPayment(payment: Payment): String

    fun getPayment(paymentId: String): Flow<Payment?>

    fun getPayments(): Flow<List<Payment>>

    suspend fun updatePayment(payment: Payment)

    suspend fun deletePayment(paymentId: String)
}