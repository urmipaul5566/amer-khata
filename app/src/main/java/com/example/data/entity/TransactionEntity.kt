package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val type: String, // "PABO" (You Gave / Customer owes) or "DIBO" (You Received / Payment from customer)
    val amount: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val paymentMethod: String = "CASH", // "CASH", "BKASH", "NAGAD", "BANK", "OTHER"
    val billImageUri: String? = null,
    val isSynced: Boolean = false
)
