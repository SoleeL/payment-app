package com.soleel.paymentapp.domain.payment

import com.soleel.paymentapp.core.model.paymentprocess.ValidationPaymentProcessData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
abstract class RequestValidationPaymentUseCaseModule {

    @Binds
    abstract fun bindRequestValidationPaymentUseCaseModule(
        impl: RequestValidationPaymentUseCaseMock
    ): IRequestValidationPaymentUseCase
}

fun interface IRequestValidationPaymentUseCase {
    operator fun invoke(): Flow<ValidationPaymentProcessData>
}

//class RequestValidationPaymentUseCase @Inject constructor() :
//    IRequestValidationPaymentUseCase {
//    override operator fun invoke(): Flow<ValidationPaymentProcessData> = ...
//}

class RequestValidationPaymentUseCaseMock @Inject constructor() : IRequestValidationPaymentUseCase {
    override fun invoke(): Flow<ValidationPaymentProcessData> = flow {
        delay(2000) // Simula una espera de 2 segundos
        emit(
            ValidationPaymentProcessData(
                isValid = true,
                message = "Validación exitosa",
                validationCode = "VAL-TEST-1234"
            )
        )
    }
}