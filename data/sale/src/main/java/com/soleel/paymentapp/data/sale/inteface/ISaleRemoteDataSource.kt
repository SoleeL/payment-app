package com.soleel.paymentapp.data.sale.inteface

import com.soleel.paymentapp.core.model.base.Sale
import kotlinx.coroutines.flow.Flow

interface ISaleRemoteDataSource {

    suspend fun createSale(sale: Sale): String

    fun getSale(saleId: String): Flow<Sale?>

    fun getSales(): Flow<List<Sale>>

    suspend fun retrySales() // TODO: POR SI VARIAS VENTAS QUEDARON PENDIENTES Y SE NECESITA REALIZAR UN REINTENTO DE REGISTRO DE TODAS

    suspend fun retrySale(saleId: String) // TODO: POR SI UNA VENTA QUEDO PENDIENTE Y CLIENTE DESEA REINTENTAR MANUALMENTE

    suspend fun updateSale()

    suspend fun deleteSale(saleId: String)
}