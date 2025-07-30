package com.soleel.paymentapp.domain.reading

import com.soleel.paymentapp.core.model.readingprocess.InterfaceReadData
import kotlinx.coroutines.flow.Flow


fun interface IContactlessReadingUseCase {
    operator fun invoke(): Flow<InterfaceReadData>
}