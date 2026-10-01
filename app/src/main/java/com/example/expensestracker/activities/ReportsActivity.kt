package com.example.expensestracker.activities

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.MediatorLiveData
import com.example.expensestracker.databinding.ActivityReportsBinding
import com.example.expensestracker.databinding.ItemCategoryReportBinding
import com.example.expensestracker.viewmodel.ExpenseViewModel
import com.example.expensestracker.utils.CurrencyUtils
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ReportsActivity : BaseActivity() {

    private lateinit var binding: ActivityReportsBinding
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        observeData()
    }

    private fun observeData() {
        viewModel.currentBalance.observe(this) { balance ->
            binding.tvCumulativeSavings.text = CurrencyUtils.formatCurrency(balance ?: 0.0, preferenceManager)
        }

        viewModel.getCategorySummary().observe(this) { list ->
            binding.llCategorySpending.removeAllViews()
            val maxTotal = list.maxOfOrNull { it.total } ?: 1.0
            val pieEntries = mutableListOf<PieEntry>()
            list.forEach { item ->
                addCategoryRow(item.category, item.total, (item.total / maxTotal * 100).toInt())
                pieEntries.add(PieEntry(item.total.toFloat(), item.category))
            }
            setupPieChart(pieEntries)
        }

        val monthlyExpenses = viewModel.getMonthlySpendingSummary()
        val allSalaries = viewModel.getAllSalaries()

        MediatorLiveData<Unit>().apply {
            var expenses: List<com.example.expensestracker.database.MonthlySpending>? = null
            var salaries: List<com.example.expensestracker.model.Salary>? = null
            fun update() {
                if (expenses != null && salaries != null) {
                    calculateAndDisplaySavingsGrowth(expenses!!, salaries!!)
                }
            }
            addSource(monthlyExpenses) { expenses = it; update() }
            addSource(allSalaries) { salaries = it; update() }
        }.observe(this) {}
        
        viewModel.getMonthlySpendingSummary().observe(this) { list ->
            val barEntries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()
            list.forEachIndexed { index, item ->
                barEntries.add(BarEntry(index.toFloat(), item.total.toFloat()))
                labels.add(item.month)
            }
            setupBarChart(barEntries, labels)
        }
    }

    private fun calculateAndDisplaySavingsGrowth(
        expenses: List<com.example.expensestracker.database.MonthlySpending>,
        salaries: List<com.example.expensestracker.model.Salary>
    ) {
        val expenseMap = expenses.associate { it.month to it.total }
        val salaryMap = salaries.associate { it.month to it.amount }
        val allMonths = (expenseMap.keys + salaryMap.keys).distinct().sorted()
        
        val lineEntries = mutableListOf<Entry>()
        var cumulativeSavings = 0.0
        
        allMonths.forEachIndexed { index, month ->
            val monthlySalary = salaryMap[month] ?: 0.0
            val monthlyExpense = expenseMap[month] ?: 0.0
            cumulativeSavings += (monthlySalary - monthlyExpense)
            lineEntries.add(Entry(index.toFloat(), cumulativeSavings.toFloat()))
        }
        setupLineChart(lineEntries, allMonths)
    }

    private fun addCategoryRow(category: String, amount: Double, progress: Int) {
        val rowBinding = ItemCategoryReportBinding.inflate(LayoutInflater.from(this), binding.llCategorySpending, false)
        rowBinding.tvCategoryName.text = category
        rowBinding.tvCategoryAmount.text = CurrencyUtils.formatCurrency(amount, preferenceManager)
        rowBinding.indicator.progress = progress
        binding.llCategorySpending.addView(rowBinding.root)
    }

    private fun setupPieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Categories").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }
        binding.pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    private fun setupBarChart(entries: List<BarEntry>, labels: List<String>) {
        val dataSet = BarDataSet(entries, "Monthly Spending").apply {
            colors = ColorTemplate.VORDIPLOM_COLORS.toList()
        }
        binding.barChart.apply {
            data = BarData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            description.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    private fun setupLineChart(entries: List<Entry>, labels: List<String>) {
        if (entries.isEmpty()) return
        val dataSet = LineDataSet(entries, "Savings Growth").apply {
            color = Color.parseColor("#0D47A1")
            setCircleColor(Color.parseColor("#0D47A1"))
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = Color.parseColor("#42A5F5")
        }
        binding.lineChartSavings.apply {
            data = LineData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            description.isEnabled = false
            animateX(1000)
            invalidate()
        }
    }
}
