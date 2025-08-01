package com.soleel.saleapp.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.soleel.paymentapp.core.database.entity.SaleEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface SaleDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(saleEntity: SaleEntity)

    @Query("SELECT * FROM sale_table")
    fun getAllSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sale_table WHERE id = :id")
    fun getSaleById(id: String): Flow<SaleEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(saleEntity: SaleEntity)

    @Delete
    suspend fun delete(saleEntity: SaleEntity)
}