package com.example.expensestracker.database

import androidx.room.*
import com.example.expensestracker.model.Salary
import kotlinx.coroutines.flow.Flow

@Dao
interface SalaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalary(salary: Salary)

    @Update
    suspend fun updateSalary(salary: Salary)

    @Query("SELECT * FROM salaries WHERE month = :month AND year = :year LIMIT 1")
    fun getSalaryByMonthYear(month: String, year: Int): Flow<Salary?>

    @Query("SELECT * FROM salaries ORDER BY updatedDate DESC LIMIT 1")
    fun getCurrentSalary(): Flow<Salary?>

    @Query("SELECT SUM(amount) FROM salaries")
    fun getTotalSalaryAmount(): Flow<Double?>

    @Query("SELECT * FROM salaries ORDER BY year DESC, month DESC")
    fun getAllSalaries(): Flow<List<Salary>>

    @Delete
    suspend fun deleteSalary(salary: Salary)

    @Query("DELETE FROM salaries")
    suspend fun deleteAllSalaries()
}
