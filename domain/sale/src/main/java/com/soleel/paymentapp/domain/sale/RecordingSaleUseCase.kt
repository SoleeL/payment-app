package com.soleel.paymentapp.domain.sale

import com.soleel.paymentapp.core.model.outcomeprocess.RecordingSaleProcessData
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
abstract class RecordingSaleUseCaseModule {

    @Binds
    abstract fun bindRecordingSaleUseCaseModule(
        impl: RecordingSaleUseCaseMock
    ): IRecordingSaleUseCase
}

fun interface IRecordingSaleUseCase {
    operator fun invoke(): Flow<RecordingSaleProcessData>
}

//class RecordingSaleUseCase @Inject constructor() :
//    IRecordingSaleUseCase {
//    override operator fun invoke(): Flow<RecordingSaleProcessData> = ...
//}

class RecordingSaleUseCaseMock @Inject constructor() : IRecordingSaleUseCase {
    override fun invoke(): Flow<RecordingSaleProcessData> = flow {
        delay(2000)
        emit(
            RecordingSaleProcessData(
                remoteSaleId = "remote-12345",
                confirmationCode = "CONF-67890",
                syncedAt = System.currentTimeMillis()
            )
        )
    }
}