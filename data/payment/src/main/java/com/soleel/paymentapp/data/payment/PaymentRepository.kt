package com.soleel.paymentapp.data.payment

import com.soleel.paymentapp.core.database.dao.PaymentDAO
import com.soleel.paymentapp.core.model.base.Payment
import com.soleel.paymentapp.data.payment.inteface.IPaymentLocalDataSource
import com.soleel.paymentapp.data.payment.util.toEntity
import com.soleel.paymentapp.data.payment.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PaymentRepository @Inject constructor(
    private val paymentDAO: PaymentDAO,
    // private val paymentNetwork: paymentNetwork,
//    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : IPaymentLocalDataSource {
    override suspend fun createPayment(payment: Payment): String {
        val entity = payment.toEntity()
        paymentDAO.insert(entity)
        return entity.id
    }

    override fun getPayment(paymentId: String): Flow<Payment?> {
        return paymentDAO.getPaymentById(paymentId).map { it.toModel() }
    }

    override fun getPayments(): Flow<List<Payment>> {
        return paymentDAO.getAllPayment().map { list -> list.map { it.toModel() } }
    }

    override suspend fun updatePayment(payment: Payment) {
        paymentDAO.update(payment.toEntity())
    }

    override suspend fun deletePayment(paymentId: String) {
        paymentDAO.getPaymentById(paymentId).firstOrNull()?.let {
            paymentDAO.delete(it)
        }
    }
}