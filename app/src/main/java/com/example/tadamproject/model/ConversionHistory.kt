package com.example.tadamproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_history")
data class ConversionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val amount: Double,
    val result: Double,
    val timestamp: Long = System.currentTimeMillis()
)
