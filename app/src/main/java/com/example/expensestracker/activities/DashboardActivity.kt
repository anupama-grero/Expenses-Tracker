package com.example.expensestracker.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensestracker.R
import com.example.expensestracker.adapter.ExpenseAdapter
import com.example.expensestracker.databinding.ActivityDashboardBinding
import com.example.expensestracker.viewmodel.DashboardViewModel
import com.example.expensestracker.utils.CurrencyUtils
import com.example.expensestracker.utils.DateUtils

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: ExpenseAdapter

    private var currentMonthlySalary = 0.0
    private var currentMonthlyExpense = 0.0
    private var isSalarySet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }

    private fun setupUI() {
        binding.tvGreeting.text = getString(R.string.hello_user)
        binding.tvDate.text = DateUtils.getCurrentDate()

        binding.btnAddExpense.setOnClickListener { navigateTo(AddExpenseActivity::class.java) }
        binding.btnManageSalary.setOnClickListener { navigateTo(ManageSalaryActivity::class.java) }
        binding.btnViewReports.setOnClickListener { navigateTo(ReportsActivity::class.java) }
        binding.btnSeeAll.setOnClickListener { navigateTo(ExpenseListActivity::class.java) }
        binding.btnSettings.setOnClickListener { navigateTo(SettingsActivity::class.java) }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
                R.id.nav_transactions -> { navigateTo(ExpenseListActivity::class.java); true }
                R.id.nav_reports -> { navigateTo(ReportsActivity::class.java); true }
                R.id.nav_settings -> { navigateTo(SettingsActivity::class.java); true }
                else -> false
            }
        }
    }

    private fun navigateTo(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { expense ->
            val intent = Intent(this, EditExpenseActivity::class.java)
            intent.putExtra("EXPENSE", expense)
            startActivity(intent)
        }
        binding.rvRecentActivity.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = this@DashboardActivity.adapter
        }
    }

    private fun observeData() {
        viewModel.currentBalance.observe(this) { balance ->
            binding.tvTotalBalance.text = CurrencyUtils.formatCurrency(balance ?: 0.0, preferenceManager)
        }

        viewModel.monthlyExpenses.observe(this) { amount ->
            currentMonthlyExpense = amount ?: 0.0
            binding.tvMonthlyExpense.text = CurrencyUtils.formatCurrency(currentMonthlyExpense, preferenceManager)
            updateSavings()
        }

        //read (observe the data)
        viewModel.currentSalary.observe(this) { salary ->
            if (salary != null) {
                currentMonthlySalary = salary.amount
                binding.tvMonthlySalary.text = CurrencyUtils.formatCurrency(currentMonthlySalary, preferenceManager)
                isSalarySet = true
            } else {
                binding.tvMonthlySalary.text = getString(R.string.no_salary_set)
                isSalarySet = false
            }
            updateSavings()
        }

        viewModel.recentTransactions.observe(this) { expenses ->
            adapter.submitList(expenses)
        }
    }

    private fun refreshUI() {
        viewModel.currentBalance.value?.let { binding.tvTotalBalance.text = CurrencyUtils.formatCurrency(it, preferenceManager) }
        binding.tvMonthlyExpense.text = CurrencyUtils.formatCurrency(currentMonthlyExpense, preferenceManager)
        if (isSalarySet) {
            binding.tvMonthlySalary.text = CurrencyUtils.formatCurrency(currentMonthlySalary, preferenceManager)
        }
        updateSavings()
        adapter.notifyDataSetChanged()
    }

    private fun updateSavings() {
        if (!isSalarySet) {
            binding.tvSavings.text = getString(R.string.not_available)
            binding.tvSavingsRate.text = "0%"
            return
        }

        val savings = currentMonthlySalary - currentMonthlyExpense
        binding.tvSavings.text = CurrencyUtils.formatCurrency(savings, preferenceManager)
        binding.tvSavings.setTextColor(ContextCompat.getColor(this, if (savings >= 0) R.color.primary else R.color.error))

        val rate = if (currentMonthlySalary > 0) (savings / currentMonthlySalary) * 100 else 0.0
        binding.tvSavingsRate.text = "${rate.toInt()}%"
    }
}
