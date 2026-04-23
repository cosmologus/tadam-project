package com.example.tadamproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tadamproject.model.ConversionHistory

@Database(entities = [ConversionHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversionHistoryDao(): ConversionHistoryDao
}
