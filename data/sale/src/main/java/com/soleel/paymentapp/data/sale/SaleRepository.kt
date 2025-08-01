package com.soleel.paymentapp.data.sale

import com.soleel.paymentapp.core.model.base.Sale
import com.soleel.paymentapp.data.sale.inteface.ISaleLocalDataSource
import com.soleel.paymentapp.data.sale.util.toEntity
import com.soleel.paymentapp.data.sale.util.toModel
import com.soleel.saleapp.core.database.dao.SaleDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SaleRepository @Inject constructor(
    private val saleDAO: SaleDAO,
    // private val paymentNetwork: paymentNetwork,
//    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : ISaleLocalDataSource {
    override suspend fun createSale(sale: Sale): String {
        val entity = sale.toEntity()
        saleDAO.insert(entity)
        return entity.id
    }

    override fun getSale(saleId: String): Flow<Sale?> {
        return saleDAO.getSaleById(saleId).map { it.toModel() }
    }

    override fun getSales(): Flow<List<Sale>> {
        return saleDAO.getAllSales().map { list -> list.map { it.toModel() } }
    }

    override suspend fun updateSale(sale: Sale) {
        saleDAO.update(sale.toEntity())
    }

    override suspend fun deleteSale(saleId: String) {
        saleDAO.getSaleById(saleId).firstOrNull()?.let {
            saleDAO.delete(it)
        }
    }
}