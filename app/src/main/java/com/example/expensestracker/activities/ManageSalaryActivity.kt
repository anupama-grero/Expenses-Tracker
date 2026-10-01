package com.example.expensestracker.activities

import android.os.Bundle
import androidx.activity.viewModels
import com.example.expensestracker.databinding.ActivityManageSalaryBinding
import com.example.expensestracker.viewmodel.SalaryViewModel
import com.example.expensestracker.utils.DateUtils

class ManageSalaryActivity : BaseActivity() {

    private lateinit var binding: ActivityManageSalaryBinding
    private val viewModel: SalaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageSalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        observeData()
        setupButtons()
    }

    private fun observeData() {
        viewModel.currentSalary.observe(this) { salary ->
            salary?.let { binding.etSalaryAmount.setText(it.amount.toString()) }
        }
    }

    private fun setupButtons() {
        binding.btnSaveSalary.setOnClickListener {
            val amountStr = binding.etSalaryAmount.text.toString()
            if (amountStr.isBlank() || (amountStr.toDoubleOrNull() ?: 0.0) <= 0.0) {
                binding.etSalaryAmount.error = "Enter a valid amount"
                return@setOnClickListener
            }
            viewModel.saveSalary(
                amountStr.toDouble(),
                DateUtils.getCurrentMonth(),
                java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            )
            showSnackbar("Salary updated successfully")
            binding.root.postDelayed({ finish() }, 1000)
        }
    }
}
