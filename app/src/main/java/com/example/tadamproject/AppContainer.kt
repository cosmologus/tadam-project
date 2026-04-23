package com.example.tadamproject

import android.content.Context
import androidx.room.Room
import com.example.tadamproject.data.CurrencyRepository
import com.example.tadamproject.data.local.AppDatabase
import com.example.tadamproject.data.preferences.ThemePreferences
import com.example.tadamproject.data.remote.FrankfurterApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "tadam_database"
    ).build()

    private val retrofit = Retrofit.Builder()
        // Frankfurter uses HTTPS, so requests are encrypted with TLS.
        .baseUrl("https://api.frankfurter.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(FrankfurterApiService::class.java)

    val themePreferences = ThemePreferences(context.applicationContext)

    val currencyRepository = CurrencyRepository(
        apiService = apiService,
        dao = database.conversionHistoryDao()
    )
}
