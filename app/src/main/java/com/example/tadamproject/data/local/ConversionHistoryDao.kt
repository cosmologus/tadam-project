package com.example.tadamproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tadamproject.model.ConversionHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionHistoryDao {
    @Insert
    suspend fun insert(conversion: ConversionHistory)

    // Room binds parameters safely, so this query style avoids SQL injection issues.
    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ConversionHistory>>
}
