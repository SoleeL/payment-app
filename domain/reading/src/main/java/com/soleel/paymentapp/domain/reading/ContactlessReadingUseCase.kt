package com.soleel.paymentapp.domain.reading

import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceEnum
import com.soleel.paymentapp.core.model.readingprocess.InterfaceReadData
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
abstract class ContactlessReadingUseCaseModule {

    @Binds
    abstract fun bindContactlessReadingUseCaseModule(
        impl: ContactlessReadingUseCaseMock
    ): IContactlessReadingUseCase
}

fun interface IContactlessReadingUseCase {
    operator fun invoke(): Flow<InterfaceReadData>
}

class ContactlessReadingUseCaseMock @Inject constructor(
    private val developerPreferences: IDeveloperPreferences
) : IContactlessReadingUseCase {
    override fun invoke(): Flow<InterfaceReadData> = flow {
        delay(5000)

        val fallbackEnabled = developerPreferences.isEnabled(DeveloperPreferenceEnum.CONTACTLESS_READER_FALLBACK)
        val invalidCardEnabled = developerPreferences.isEnabled(DeveloperPreferenceEnum.CONTACTLESS_READER_INVALID_CARD)

        when {
            fallbackEnabled -> throw InterfaceFallbackException()
            invalidCardEnabled -> throw InvalidCardException()

            else -> emit(
                InterfaceReadData(
                    cardNumber = "1234 5678 9012 3456",
                    cardBrand = "VISA DEBIT",
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
}

class InterfaceFallbackException(message: String = "EMVCo fallback: cambiar a otra interfaz") : Exception(message)
class InvalidCardException(message: String = "EMVCo: tarjeta invalida") : Exception(message)