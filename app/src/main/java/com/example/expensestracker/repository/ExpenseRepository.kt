package com.example.expensestracker.repository

import com.example.expensestracker.database.ExpenseDao
import com.example.expensestracker.model.Expense
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getRecentTransactions(): Flow<List<Expense>> = expenseDao.getRecentTransactions()

    fun getTotalExpenseAmount(): Flow<Double?> = expenseDao.getTotalExpenseAmount()

    fun getMonthlyExpenses(month: String): Flow<Double?> = expenseDao.getMonthlyExpenses(month)

    fun getTodayExpenses(today: String): Flow<Double?> = expenseDao.getTodayExpenses(today)

    fun searchExpenses(query: String): Flow<List<Expense>> = expenseDao.searchExpenses(query)

    fun filterByCategory(category: String): Flow<List<Expense>> = expenseDao.getExpensesByCategory(category)

    fun getCategorySummary() = expenseDao.getCategorySummary()

    fun getMonthlySpendingSummary() = expenseDao.getMonthlySpendingSummary()

    suspend fun insert(expense: Expense) = expenseDao.insertExpense(expense)

    suspend fun update(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun delete(expense: Expense) = expenseDao.deleteExpense(expense)

    suspend fun deleteAll() = expenseDao.deleteAllExpenses()
}
