package com.soleel.paymentapp.data.sale.inteface

import com.soleel.paymentapp.core.model.base.Sale
import kotlinx.coroutines.flow.Flow

interface ISaleLocalDataSource {

    suspend fun createSale(sale: Sale): String

    fun getSale(saleId: String): Flow<Sale?>

    fun getSales(): Flow<List<Sale>>

    suspend fun updateSale(sale: Sale)

    suspend fun deleteSale(saleId: String)
}