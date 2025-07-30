package com.soleel.paymentapp.domain.reading

import com.soleel.paymentapp.core.model.readingprocess.InterfaceReadData
import kotlinx.coroutines.flow.Flow

fun interface IContactReadingUseCase {
    operator fun invoke(): Flow<InterfaceReadData>
}




//private fun getContactReadingUiState(): Flow<ReadingUiState> = flow(
//    block = {
//        emit(ReadingUiState.Reading)
//        delay(5_000)
//
//        if (!false) { // TODO: Pendiente implementacion con shared preference para pruebas
//            emit(ReadingUiState.Success)
//        } else {
//            emit(ReadingUiState.Failure)
//        }
//    }
//)