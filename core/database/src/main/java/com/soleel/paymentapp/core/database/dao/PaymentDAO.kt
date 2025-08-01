package com.soleel.paymentapp.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.soleel.paymentapp.core.database.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PaymentDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(paymentEntity: PaymentEntity)

    @Query("SELECT * FROM payment_table")
    fun getAllPayment(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payment_table WHERE id = :id")
    fun getPaymentById(id: String): Flow<PaymentEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(paymentEntity: PaymentEntity)

    @Delete
    suspend fun delete(paymentEntity: PaymentEntity)
}
