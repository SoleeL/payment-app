package com.soleel.paymentapp.domain.pinpad

import com.soleel.paymentapp.core.model.pinpadprocess.PinBlockData
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
abstract class PinBlockRequestUseCaseModule {

    @Binds
    abstract fun bindPinBlockRequestUseCaseModule(
        impl: PinBlockRequestUseCaseMock
    ): IPinBlockRequestUseCase
}

fun interface IPinBlockRequestUseCase {
    operator fun invoke(): Flow<PinBlockData>
}

class PinBlockRequestUseCaseMock @Inject constructor() : IPinBlockRequestUseCase {
    override fun invoke(): Flow<PinBlockData> = flow {
        delay(2000)
        emit(
            PinBlockData(
                pinBlock = "1234567890ABCDEF",
                ksn = "FFFF9876543210E00000"
            )
        )
    }
}

class InvalidKSNException(message: String = "Kernel: error al utilizar Pinpad por KSN invalido") : Exception(message)