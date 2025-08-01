package com.soleel.paymentapp.domain.sale

import com.soleel.paymentapp.core.model.base.Sale
import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceEnum
import com.soleel.paymentapp.data.preferences.developer.IDeveloperPreferences
import com.soleel.paymentapp.data.sale.inteface.ISaleLocalDataSource
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
    operator fun invoke(
        paymentUUID: UUID,
        subtotal: Int,
        cashChangeSelected: Int?,
        debitCashback: Int?,
        source: String?,
    ): Flow<Sale>
}

//class StoreSaleUseCase @Inject constructor() :
//    IStoreSaleUseCase {
//    override operator fun invoke(): Flow<StoreSaleProcessData> = ...
//}

class StoreSaleUseCaseMock @Inject constructor(
    private val saleRepository: ISaleLocalDataSource,

    private val developerPreferences: IDeveloperPreferences
) : IStoreSaleUseCase {
    override fun invoke(
        paymentUUID: UUID,
        subtotal: Int,
        cashChangeSelected: Int?,
        debitCashback: Int?,
        source: String?,
    ): Flow<Sale> = flow {
        delay(2000)

        val saleSaveFailByLocalErrorEnabled = developerPreferences.isEnabled(
            DeveloperPreferenceEnum.SALE_SAVE_FAIL_BY_LOCAL_ERROR
        )
        when {
            saleSaveFailByLocalErrorEnabled -> throw SaleSaveLocalErrorException()
            else -> {
                val sale = Sale(
                    id = UUID.randomUUID(),
                    paymentId = paymentUUID,
                    subtotal = subtotal,
                    cashChangeSelected = cashChangeSelected,
                    debitCashback = debitCashback,
                    source = source ?: BuildConfig.APP_PACKAGE_NAME,
                    versionApp = BuildConfig.VERSION_NAME,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
                saleRepository.createSale(sale)
                emit(sale)
            }
        }
    }
}

class SaleSaveLocalErrorException(message: String = "Terminal: venta no almacenada") :
    Exception(message)