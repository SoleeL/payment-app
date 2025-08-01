package com.soleel.paymentapp.data.sale.di

import com.soleel.paymentapp.data.sale.SaleRepository
import com.soleel.paymentapp.data.sale.inteface.ISaleLocalDataSource
import com.soleel.saleapp.core.database.dao.SaleDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SaleModule {

    @Provides
    fun provideSaleLocalDataSource(saleDAO: SaleDAO): ISaleLocalDataSource {
        return SaleRepository(saleDAO)
    }
}