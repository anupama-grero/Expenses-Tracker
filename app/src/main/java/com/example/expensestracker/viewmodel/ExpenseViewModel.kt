package com.example.expensestracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.expensestracker.database.ExpenseDatabase
import com.example.expensestracker.model.Expense
import com.example.expensestracker.repository.ExpenseRepository
import com.example.expensestracker.repository.SalaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository: ExpenseRepository
    private val salaryRepository: SalaryRepository

    val allExpenses: LiveData<List<Expense>>
    val currentBalance: LiveData<Double> // Still need this for Reports if shown there

    init {
        val database = ExpenseDatabase.getDatabase(application)
        expenseRepository = ExpenseRepository(database.expenseDao())
        salaryRepository = SalaryRepository(database.salaryDao())

        allExpenses = expenseRepository.getAllExpenses().asLiveData()
        
        currentBalance = MediatorLiveData<Double>().apply {
            var totalExp = 0.0
            var totalSal = 0.0
            fun update() { value = totalSal - totalExp }
            addSource(database.expenseDao().getTotalExpenseAmount().asLiveData()) { totalExp = it ?: 0.0; update() }
            addSource(database.salaryDao().getTotalSalaryAmount().asLiveData()) { totalSal = it ?: 0.0; update() }
        }
    }

    fun insert(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        expenseRepository.insert(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        expenseRepository.update(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        expenseRepository.delete(expense)
    }

    fun searchExpenses(query: String): LiveData<List<Expense>> =
        expenseRepository.searchExpenses(query).asLiveData()

    fun filterByCategory(category: String): LiveData<List<Expense>> =
        expenseRepository.filterByCategory(category).asLiveData()

    fun getCategorySummary() = expenseRepository.getCategorySummary().asLiveData()

    fun getMonthlySpendingSummary() = expenseRepository.getMonthlySpendingSummary().asLiveData()

    fun getAllSalaries() = salaryRepository.getAllSalaries().asLiveData()

    fun deleteAllExpenses() = viewModelScope.launch(Dispatchers.IO) {
        expenseRepository.deleteAll()
    }

    fun resetAllData(onSuccess: () -> Unit, onError: (Exception) -> Unit) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                expenseRepository.deleteAll()
                salaryRepository.deleteAll()
            }
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }
}
