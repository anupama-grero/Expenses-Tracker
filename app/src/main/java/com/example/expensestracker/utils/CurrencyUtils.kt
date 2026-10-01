package com.example.expensestracker.utils

import java.util.*

object CurrencyUtils {
    fun formatCurrency(amount: Double, preferenceManager: PreferenceManager): String {
        val symbol = preferenceManager.getCurrencySymbol()
        return "$symbol${String.format(Locale.US, "%,.2f", amount)}"
    }
}
