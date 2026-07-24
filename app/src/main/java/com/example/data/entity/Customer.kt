package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val customerType: String = "CUSTOMER", // "CUSTOMER" or "SUPPLIER"
    val totalBalance: Double = 0.0, // Positive = "Pabo" (I get / customer owes me), Negative = "Dibo" (I owe customer)
    val createdAt: Long = System.currentTimeMillis(),
    val lastTransactionAt: Long = System.currentTimeMillis()
)
