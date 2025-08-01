package com.soleel.paymentapp.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "payment_table",
)
data class PaymentEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "method") var method: Int,
    @ColumnInfo(name = "amount") var amount: Int,
    @ColumnInfo(name = "instalments") var instalments: Int?,
    @ColumnInfo(name = "applicationLabel") var applicationLabel: String,
    @ColumnInfo(name = "aid") var aid: String,
    @ColumnInfo(name = "last4") var last4: String,
    @ColumnInfo(name = "sequenceNumber") var sequenceNumber: String,
    @ColumnInfo(name = "authCode") var authCode: String,
    @ColumnInfo(name = "created_at") var createdAt: Long,
    @ColumnInfo(name = "updated_at") var updatedAt: Long,
)