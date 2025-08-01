package com.soleel.paymentapp.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale_table")


data class SaleEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "payment_id") val paymentId: String,

    @ColumnInfo(name = "subtotal") val subtotal: Int,
    @ColumnInfo(name = "tip") val tip: Int,
    @ColumnInfo(name = "debit_cashback") val debitCashback: Int,

    @ColumnInfo(name = "cash_change_selected") val cashChangeSelected: Int?,

    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "version_app") val versionApp: String,

    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)