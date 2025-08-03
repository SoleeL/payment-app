package com.soleel.paymentapp.data.payment.di

import com.soleel.paymentapp.core.database.dao.PaymentDAO
import com.soleel.paymentapp.data.payment.PaymentRepository
import com.soleel.paymentapp.data.payment.interfaces.IPaymentLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    @Provides
    fun providePaymentLocalDataSource(paymentDAO: PaymentDAO): IPaymentLocalDataSource {
        return PaymentRepository(paymentDAO)
    }
}