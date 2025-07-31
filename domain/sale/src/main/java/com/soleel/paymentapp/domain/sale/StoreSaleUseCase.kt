package com.soleel.paymentapp.domain.sale

import com.soleel.paymentapp.core.model.Sale
import com.soleel.paymentapp.core.model.outcomeprocess.StoreSaleProcessData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

class StoreSaleUseCaseMock @Inject constructor() : IStoreSaleUseCase {
    override fun invoke(): Flow<StoreSaleProcessData> = flow {
        delay(2000)
        emit(
            StoreSaleProcessData(
                localSaleId = UUID.randomUUID(),
                sale = Sale(calculatorTotal = 7000.0f),
                timestamp = System.currentTimeMillis()
            )
        )
    }
}