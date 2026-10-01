package com.example.expensestracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val description: String,
    val date: String,
    val paymentMethod: String,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
