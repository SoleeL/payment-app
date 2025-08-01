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

        val fallbackEnabled =
            developerPreferences.isEnabled(DeveloperPreferenceEnum.CONTACTLESS_READER_FALLBACK)
        val invalidCardEnabled =
            developerPreferences.isEnabled(DeveloperPreferenceEnum.CONTACTLESS_READER_INVALID_CARD)

        when {
            fallbackEnabled -> throw InterfaceFallbackException()
            invalidCardEnabled -> throw InvalidCardException()

            else -> {
                val labels = listOf("MASTERCARD DEBIT", "VISA CREDIT", "AMEX", "DISCOVER")
                val aids =
                    listOf("A0000000041010", "A0000000031010", "A00000002501", "A0000001523010")

                val randomLabel = labels.random()
                val randomAid = aids.random()
                val randomLast4 = (1000..9999).random().toString()
                val randomExp = "0${(1..9).random()}/${(26..30).random()}"

                val randomData = InterfaceReadData(
                    applicationLabel = randomLabel,
                    aid = randomAid,
                    last4 = randomLast4,
                    expirationDate = randomExp,
                    isValid = true
                )

                emit(randomData)
            }
        }
    }
}

class InterfaceFallbackException(message: String = "EMVCo fallback: cambiar a otra interfaz") :
    Exception(message)

class InvalidCardException(message: String = "EMVCo: tarjeta invalida") : Exception(message)