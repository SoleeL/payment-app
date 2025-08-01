package com.soleel.paymentapp.domain.payment

import com.soleel.paymentapp.core.model.paymentprocess.PaymentProcessData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject


@Module
@InstallIn(SingletonComponent::class)
abstract class SavePaymentUseCaseModule {

    @Binds
    abstract fun bindSavePaymentUseCaseModule(
        impl: SavePaymentUseCaseMock
    ): ISavePaymentUseCase
}

fun interface ISavePaymentUseCase {
    operator fun invoke(): Flow<PaymentProcessData>
}

//class SavePaymentUseCase @Inject constructor() :
//    ISavePaymentUseCase {
//    override operator fun invoke(): Flow<PaymentProcessData> = ...
//}

class SavePaymentUseCaseMock @Inject constructor() : ISavePaymentUseCase {
    override fun invoke(): Flow<PaymentProcessData> = flow {
        delay(2000)
        emit(
            PaymentProcessData(
                id = "PMT-TEST-9999",
                amount = 12345.67,
                currency = "CLP",
                savedAt = LocalDateTime.now(),
                reference = "TEST-REF-001"
            )
        )
    }
}