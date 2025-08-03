package com.soleel.paymentapp.domain.pinpad.pinblockrequest.interfaces

import com.soleel.paymentapp.core.model.pinpadprocess.PinBlockData
import kotlinx.coroutines.flow.Flow

fun interface IPinBlockRequestUseCase {
    operator fun invoke(): Flow<PinBlockData>
}
