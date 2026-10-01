package com.example.expensestracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("expense_tracker_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "key_theme"
        private const val KEY_CURRENCY = "key_currency"
        
        const val CURRENCY_USD = "USD"
        const val CURRENCY_LKR = "LKR"
        const val CURRENCY_EUR = "EUR"
        const val CURRENCY_GBP = "GBP"
        const val CURRENCY_INR = "INR"
    }

    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME, mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setCurrency(currency: String) {
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
    }

    fun getCurrency(): String {
        return prefs.getString(KEY_CURRENCY, CURRENCY_USD) ?: CURRENCY_USD
    }
    
    fun getCurrencySymbol(): String {
        return when (getCurrency()) {
            CURRENCY_LKR -> "Rs. "
            CURRENCY_EUR -> "€"
            CURRENCY_GBP -> "£"
            CURRENCY_INR -> "₹"
            else -> "$"
        }
    }
}
