package com.soleel.paymentapp.domain.payment

import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceEnum
import com.soleel.paymentapp.core.model.paymentprocess.ValidationPaymentProcessData
import com.soleel.paymentapp.data.preferences.developer.IDeveloperPreferences
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

class RequestValidationPaymentUseCaseMock @Inject constructor(
    private val developerPreferences: IDeveloperPreferences
) : IRequestValidationPaymentUseCase {
    override fun invoke(): Flow<ValidationPaymentProcessData> = flow {
        delay(2000) // Simula una espera de 2 segundos

        val paymentRejectedEnabled = developerPreferences.isEnabled(
            DeveloperPreferenceEnum.PAYMENT_VALIDATION_FAIL_BY_ACQUIRER_ERROR
        )

        when {
            paymentRejectedEnabled -> throw PaymentRejectedException()
            else -> {
                val randomSequenceNumber = (10000000..99999999).random()
                val authCode = (1..6)
                    .map { (0..15).random() }
                    .joinToString("") { it.toString(16).uppercase() }

                emit(
                    ValidationPaymentProcessData(
                        isValid = true,
                        message = "Validación exitosa",
                        authCode = authCode,
                        sequenceNumber = randomSequenceNumber.toString()
                    )
                )
            }
        }
    }
}

class PaymentRejectedException(message: String = "Adquire: pago rechazado") : Exception(message)