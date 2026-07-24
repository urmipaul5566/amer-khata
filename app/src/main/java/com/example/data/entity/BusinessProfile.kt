package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "আমার খাতা স্টোর",
    val ownerName: String = "প্রোপাইটর (Owner)",
    val phone: String = "",
    val address: String = "",
    val currency: String = "৳"
)
