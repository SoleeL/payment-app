package com.soleel.paymentapp.domain.sale

import com.soleel.paymentapp.core.model.enums.DeveloperPreferenceEnum
import com.soleel.paymentapp.core.model.outcomeprocess.RecordingSaleProcessData
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

class RecordingSaleUseCaseMock @Inject constructor(
    private val developerPreferences: IDeveloperPreferences
) : IRecordingSaleUseCase {
    override fun invoke(): Flow<RecordingSaleProcessData> = flow {
        delay(2000)

        val saleSaveFailByServiceErrorEnabled = developerPreferences.isEnabled(
            DeveloperPreferenceEnum.SALE_SAVE_FAIL_BY_SERVICE_ERROR
        )
        when {
            saleSaveFailByServiceErrorEnabled -> throw SaleSaveServiceErrorException()
            else -> {
                emit(
                    RecordingSaleProcessData(
                        remoteSaleId = "remote-12345",
                        confirmationCode = "CONF-67890",
                        syncedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}

class SaleSaveServiceErrorException(message: String = "Servicio: venta no registrada") : Exception(message)