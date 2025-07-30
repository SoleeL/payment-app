package com.soleel.paymentapp.domain.payment

import com.soleel.paymentapp.core.model.paymentprocess.ConfirmationPaymentProcessData
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
abstract class RequestConfirmingPaymentUseCaseModule {

    @Binds
    abstract fun bindRequestConfirmingPaymentUseCaseModule(
        impl: RequestConfirmingPaymentUseCaseMock
    ): IRequestConfirmingPaymentUseCase
}

fun interface IRequestConfirmingPaymentUseCase {
    operator fun invoke(): Flow<ConfirmationPaymentProcessData>
}

//class RequestConfirmingPaymentUseCase @Inject constructor() :
//    IRequestConfirmingPaymentUseCase {
//    override operator fun invoke(): Flow<ConfirmationPaymentProcessData> = ...
//}

class RequestConfirmingPaymentUseCaseMock @Inject constructor() : IRequestConfirmingPaymentUseCase {
    override fun invoke(): Flow<ConfirmationPaymentProcessData> = flow {
        delay(2000)
        emit(
            ConfirmationPaymentProcessData(
                isConfirmed = true,
                confirmationCode = "CNF-TEST-5678",
                timestamp = LocalDateTime.now(),
                message = "Confirmación completada"
            )
        )
    }
}