package com.example.expensestracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "salaries",
    indices = [Index(value = ["month", "year"], unique = true)]
)
data class Salary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val month: String,
    val year: Int,
    val updatedDate: Long = System.currentTimeMillis()
)
