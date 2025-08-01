package com.soleel.paymentapp.domain.payment

import com.soleel.paymentapp.core.model.base.Payment
import com.soleel.paymentapp.core.model.enums.PaymentMethodEnum
import com.soleel.paymentapp.data.payment.inteface.IPaymentLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject


@Module
@InstallIn(SingletonComponent::class)
abstract class StorePaymentUseCaseModule {

    @Binds
    abstract fun bindStorePaymentUseCaseModule(
        impl: StorePaymentUseCaseMock
    ): IStorePaymentUseCase
}

fun interface IStorePaymentUseCase {
    operator fun invoke(
        method: PaymentMethodEnum,
        amount: Int,
        instalments: Int?,
        applicationLabel: String,
        aid: String,
        last4: String,
        sequenceNumber: String,
        authCode: String
    ): Flow<Payment>
}

//class StorePaymentUseCase @Inject constructor() :
//    IStorePaymentUseCase {
//    override operator fun invoke(): Flow<PaymentProcessData> = ...
//}

class StorePaymentUseCaseMock @Inject constructor(
    private val paymentRepository: IPaymentLocalDataSource
) : IStorePaymentUseCase {
    override fun invoke(
        method: PaymentMethodEnum,
        amount: Int,
        instalments: Int?,
        applicationLabel: String,
        aid: String,
        last4: String,
        sequenceNumber: String,
        authCode: String
    ): Flow<Payment> = flow {
        delay(2000)

        val payment = Payment(
            id = UUID.randomUUID(),
            method = method,
            amount = amount,
            instalments = instalments,
            applicationLabel = applicationLabel,
            aid = aid,
            last4 = last4,
            sequenceNumber = sequenceNumber,
            authCode = authCode,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        paymentRepository.createPayment(payment)
        emit(payment)
    }
}