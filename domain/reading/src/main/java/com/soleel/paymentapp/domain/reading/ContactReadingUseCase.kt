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
abstract class ContactReadingUseCaseModule {

    @Binds
    abstract fun bindContactReadingUseCaseModule(
        impl: ContactReadingUseCaseMock
    ): IContactReadingUseCase
}

fun interface IContactReadingUseCase {
    operator fun invoke(): Flow<InterfaceReadData>
}

class ContactReadingUseCaseMock @Inject constructor() : IContactReadingUseCase {
    override fun invoke(): Flow<InterfaceReadData> = flow {
        delay(5000)
        emit(
            InterfaceReadData(
                cardNumber = "1234 5678 9012 3456",
                cardHolderName = "Juan Pérez",
                expirationDate = "12/25",
                isValid = true,
                additionalInfo = mapOf(
                    "transactionId" to "TX123456789",
                    "authCode" to "AUTH98765"
                )
            )
        )
    }
}