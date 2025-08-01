package com.soleel.paymentapp.domain.sale

import com.soleel.paymentapp.core.model.base.Sale
import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceEnum
import com.soleel.paymentapp.core.model.outcomeprocess.StoreSaleProcessData
import com.soleel.paymentapp.data.preferences.developer.IDeveloperPreferences
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
abstract class StoreSaleUseCaseModule {

    @Binds
    abstract fun bindStoreSaleUseCaseModule(
        impl: StoreSaleUseCaseMock
    ): IStoreSaleUseCase
}

fun interface IStoreSaleUseCase {
    operator fun invoke(): Flow<StoreSaleProcessData>
}

//class StoreSaleUseCase @Inject constructor() :
//    IStoreSaleUseCase {
//    override operator fun invoke(): Flow<StoreSaleProcessData> = ...
//}

class StoreSaleUseCaseMock @Inject constructor(
    private val developerPreferences: IDeveloperPreferences
) : IStoreSaleUseCase {
    override fun invoke(): Flow<StoreSaleProcessData> = flow {
        delay(2000)

        val saleSaveFailByLocalErrorEnabled = developerPreferences.isEnabled(
            DeveloperPreferenceEnum.SALE_SAVE_FAIL_BY_LOCAL_ERROR
        )
        when {
            saleSaveFailByLocalErrorEnabled -> throw SaleSaveLocalErrorException()
            else -> {
                emit(
                    StoreSaleProcessData(
                        saleUUID = UUID.randomUUID(),
                        sale = Sale(
                            id = UUID.randomUUID(),
                            paymentId = UUID.randomUUID(),
                            subtotal = 5000,
                            tip = 1000,
                            debitCashback = 1000,
                            cashChangeSelected = null,
                            source = "MOCK",
                            versionApp = "1.0.0",
                            createdAt = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now()
                        ),
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}

class SaleSaveLocalErrorException(message: String = "Terminal: venta no almacenada") : Exception(message)