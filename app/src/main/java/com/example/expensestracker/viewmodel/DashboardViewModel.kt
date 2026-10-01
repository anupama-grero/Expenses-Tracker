package com.example.expensestracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.expensestracker.database.ExpenseDatabase
import com.example.expensestracker.model.Expense
import com.example.expensestracker.model.Salary
import com.example.expensestracker.repository.ExpenseRepository
import com.example.expensestracker.repository.SalaryRepository
import com.example.expensestracker.utils.DateUtils
import java.util.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository: ExpenseRepository
    private val salaryRepository: SalaryRepository

    val recentTransactions: LiveData<List<Expense>>
    val currentSalary: LiveData<Salary?>
    val monthlyExpenses: LiveData<Double?>
    val todayExpenses: LiveData<Double?>

    val currentBalance: LiveData<Double> = MediatorLiveData<Double>().apply {
        var totalExp = 0.0
        var totalSal = 0.0
        fun update() { value = totalSal - totalExp }
        
        addSource(ExpenseDatabase.getDatabase(application).expenseDao().getTotalExpenseAmount().asLiveData()) {
            totalExp = it ?: 0.0
            update()
        }
        addSource(ExpenseDatabase.getDatabase(application).salaryDao().getTotalSalaryAmount().asLiveData()) {
            totalSal = it ?: 0.0
            update()
        }
    }

    init {
        val database = ExpenseDatabase.getDatabase(application)
        expenseRepository = ExpenseRepository(database.expenseDao())
        salaryRepository = SalaryRepository(database.salaryDao())

        recentTransactions = expenseRepository.getRecentTransactions().asLiveData()
        currentSalary = salaryRepository.getCurrentSalary().asLiveData()
        monthlyExpenses = expenseRepository.getMonthlyExpenses(DateUtils.getCurrentMonth()).asLiveData()
        todayExpenses = expenseRepository.getTodayExpenses(DateUtils.getCurrentDate()).asLiveData()
    }
}
