package com.example.expensestracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.expensestracker.database.ExpenseDatabase
import com.example.expensestracker.model.Salary
import com.example.expensestracker.repository.SalaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SalaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SalaryRepository
    val currentSalary: LiveData<Salary?>

    init {
        val salaryDao = ExpenseDatabase.getDatabase(application).salaryDao()
        repository = SalaryRepository(salaryDao)
        currentSalary = repository.getCurrentSalary().asLiveData()
    }

    fun saveSalary(amount: Double, month: String, year: Int) = viewModelScope.launch(Dispatchers.IO) {
        val salary = Salary(amount = amount, month = month, year = year)
        repository.insert(salary)
    }
}
