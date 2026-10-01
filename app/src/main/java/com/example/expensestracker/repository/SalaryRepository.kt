package com.example.expensestracker.repository

import com.example.expensestracker.database.SalaryDao
import com.example.expensestracker.model.Salary
import kotlinx.coroutines.flow.Flow

class SalaryRepository(private val salaryDao: SalaryDao) {

    fun getCurrentSalary(): Flow<Salary?> = salaryDao.getCurrentSalary()

    fun getTotalSalaryAmount(): Flow<Double?> = salaryDao.getTotalSalaryAmount()

    fun getAllSalaries(): Flow<List<Salary>> = salaryDao.getAllSalaries()

    suspend fun insert(salary: Salary) = salaryDao.insertSalary(salary)

    suspend fun update(salary: Salary) = salaryDao.updateSalary(salary)

    suspend fun deleteAll() = salaryDao.deleteAllSalaries()
}
