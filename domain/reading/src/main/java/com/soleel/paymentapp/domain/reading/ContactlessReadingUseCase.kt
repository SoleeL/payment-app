package com.soleel.paymentapp.domain.reading

import com.soleel.paymentapp.core.model.readingprocess.InterfaceReadData
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
abstract class ContactlessReadingUseCaseModule {

    @Binds
    abstract fun bindContactlessReadingUseCaseModule(
        impl: ContactlessReadingUseCaseMock
    ): IContactlessReadingUseCase
}

fun interface IContactlessReadingUseCase {
    operator fun invoke(): Flow<InterfaceReadData>
}

class ContactlessReadingUseCaseMock @Inject constructor() : IContactlessReadingUseCase {
    override fun invoke(): Flow<InterfaceReadData> = flow {
        delay(5000)
        throw InterfaceFallbackException()
    }
}

class InterfaceFallbackException(message: String = "EMVCo fallback: cambiar a otra interfaz") : Exception(message)
class InvalidCardException(message: String = "EMVCo: tarjeta invalida") : Exception(message)
class PaymentRejectedException(message: String = "Adquire: pago rechazado") : Exception(message)