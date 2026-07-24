package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BusinessProfileDao
import com.example.data.dao.CashbookDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.TransactionDao
import com.example.data.entity.BusinessProfile
import com.example.data.entity.CashbookEntry
import com.example.data.entity.Customer
import com.example.data.entity.TransactionEntity

@Database(
    entities = [
        Customer::class,
        TransactionEntity::class,
        CashbookEntry::class,
        BusinessProfile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
    abstract fun cashbookDao(): CashbookDao
    abstract fun businessProfileDao(): BusinessProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "amer_khata_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
