package com.example.expensestracker.utils

object AppUtils {
    fun getCategoryIcon(category: String): Int {
        return when (category) {
            "Food" -> android.R.drawable.ic_menu_gallery
            "Transport" -> android.R.drawable.ic_menu_directions
            "Shopping" -> android.R.drawable.ic_menu_view
            "Bills" -> android.R.drawable.ic_menu_agenda
            "Entertainment" -> android.R.drawable.ic_menu_slideshow
            "Health" -> android.R.drawable.ic_menu_my_calendar
            else -> android.R.drawable.ic_menu_help
        }
    }
}
