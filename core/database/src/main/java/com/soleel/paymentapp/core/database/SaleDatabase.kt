package com.soleel.paymentapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.soleel.paymentapp.core.database.dao.PaymentDAO
import com.soleel.paymentapp.core.database.entity.PaymentEntity
import com.soleel.paymentapp.core.database.entity.SaleEntity
import com.soleel.saleapp.core.database.dao.SaleDAO

@Database(
    entities = [
        PaymentEntity::class,
        SaleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SaleDatabase : RoomDatabase() {

    abstract fun paymentDAO(): PaymentDAO
    abstract fun saleDAO(): SaleDAO

}