package com.soleel.paymentapp.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.soleel.paymentapp.core.database.SaleDatabase
import com.soleel.paymentapp.core.database.dao.PaymentDAO
import com.soleel.saleapp.core.database.dao.SaleDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SaleDatabaseModule {

    @Singleton
    @Provides
    fun provideSaleDatabase(@ApplicationContext context: Context): SaleDatabase {
        return Room.databaseBuilder(
            context,
            SaleDatabase::class.java,
            "sale_database.db"
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @Provides
    fun providePaymentDAO(database: SaleDatabase): PaymentDAO = database.paymentDAO()

    @Provides
    fun provideSaleDAO(database: SaleDatabase): SaleDAO = database.saleDAO()

}
