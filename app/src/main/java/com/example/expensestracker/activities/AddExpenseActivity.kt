package com.example.expensestracker.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.example.expensestracker.R
import com.example.expensestracker.databinding.ActivityAddExpenseBinding
import com.example.expensestracker.model.Expense
import com.example.expensestracker.viewmodel.ExpenseViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        setupCategoryDropdown()
        setupDatePicker()
        setupButtons()
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf("Food", "Transport", "Shopping", "Entertainment", "Health", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.autoCompleteCategory.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.etDate.setText(sdf.format(Date(selection)))
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                saveExpense()
            }
        }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        if (binding.etExpenseName.text.isNullOrBlank()) {
            binding.etExpenseName.error = "Title required"
            isValid = false
        }
        val amountStr = binding.etAmount.text.toString()
        if (amountStr.isBlank() || (amountStr.toDoubleOrNull() ?: 0.0) <= 0.0) {
            binding.etAmount.error = "Enter a valid amount"
            isValid = false
        }
        if (binding.autoCompleteCategory.text.isNullOrBlank()) {
            binding.autoCompleteCategory.error = "Category required"
            isValid = false
        }
        if (binding.etDate.text.isNullOrBlank()) {
            binding.etDate.error = "Date required"
            isValid = false
        }
        return isValid
    }
    //create expenses
    private fun saveExpense() {
        val expense = Expense(
            title = binding.etExpenseName.text.toString(),
            amount = binding.etAmount.text.toString().toDouble(),
            category = binding.autoCompleteCategory.text.toString(),
            description = binding.etNotes.text.toString(),
            date = binding.etDate.text.toString(),
            paymentMethod = "Cash"
        )
        viewModel.insert(expense)
        showSnackbar("Expense saved successfully")
        binding.root.postDelayed({ finish() }, 1000)
    }
}
