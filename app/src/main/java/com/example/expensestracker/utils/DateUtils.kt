package com.example.expensestracker.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val DATE_FORMAT = "MMM dd, yyyy"
    private const val MONTH_FORMAT = "MMM"

    fun getCurrentDate(): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
    }

    fun getCurrentMonth(): String {
        return SimpleDateFormat(MONTH_FORMAT, Locale.getDefault()).format(Date())
    }

    fun formatDate(date: Long): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date(date))
    }
}
