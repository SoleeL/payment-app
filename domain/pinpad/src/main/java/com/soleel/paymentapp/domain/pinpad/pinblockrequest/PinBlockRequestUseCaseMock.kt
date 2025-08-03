package com.soleel.paymentapp.domain.pinpad.pinblockrequest

import com.soleel.paymentapp.core.model.pinpadprocess.PinBlockData
import com.soleel.paymentapp.domain.pinpad.pinblockrequest.interfaces.IPinBlockRequestUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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