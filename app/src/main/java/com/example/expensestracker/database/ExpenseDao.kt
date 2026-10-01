package com.example.expensestracker.database

import androidx.room.*
import com.example.expensestracker.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense?

    @Query("SELECT * FROM expenses WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchExpenses(query: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY createdAt DESC")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    // Dashboard Queries
    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenseAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE date LIKE :monthYear || '%'")
    fun getMonthlyExpenses(monthYear: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :today")
    fun getTodayExpenses(today: String): Flow<Double?>

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC LIMIT 5")
    fun getRecentTransactions(): Flow<List<Expense>>
    
    // Reports Queries
    @Query("SELECT SUM(amount) as total, category FROM expenses GROUP BY category")
    fun getCategorySummary(): Flow<List<CategorySpending>>

    @Query("SELECT SUM(amount) as total, SUBSTR(date, 1, 3) as month FROM expenses GROUP BY month")
    fun getMonthlySpendingSummary(): Flow<List<MonthlySpending>>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}

data class CategorySpending(
    val category: String,
    val total: Double
)

data class MonthlySpending(
    val month: String,
    val total: Double
)
