package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cashbook")
data class CashbookEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "CASH_IN" or "CASH_OUT"
    val amount: Double,
    val category: String = "General",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
