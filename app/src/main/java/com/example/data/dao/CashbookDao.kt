package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.CashbookEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CashbookDao {
    @Query("SELECT * FROM cashbook ORDER BY timestamp DESC")
    fun getAllCashbookEntries(): Flow<List<CashbookEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashbookEntry(entry: CashbookEntry): Long

    @Query("SELECT SUM(amount) FROM cashbook WHERE type = 'CASH_IN'")
    fun getTotalCashIn(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM cashbook WHERE type = 'CASH_OUT'")
    fun getTotalCashOut(): Flow<Double?>
}
