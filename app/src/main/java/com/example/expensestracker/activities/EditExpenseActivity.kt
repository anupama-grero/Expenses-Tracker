package com.example.expensestracker.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.example.expensestracker.databinding.ActivityEditExpenseBinding
import com.example.expensestracker.model.Expense
import com.example.expensestracker.viewmodel.ExpenseViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class EditExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityEditExpenseBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private var currentExpense: Expense? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        currentExpense = intent.getSerializableExtra("EXPENSE") as? Expense
        
        if (currentExpense == null) {
            finish()
            return
        }

        setupToolbar(binding.toolbar)
        setupCategoryDropdown()
        setupDatePicker()
        setupButtons()
        fillData()
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
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.etDate.setText(sdf.format(Date(selection)))
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }
    }

    private fun setupButtons() {
        binding.btnUpdate.setOnClickListener { if (validateInput()) updateExpense() }
        binding.btnDelete.setOnClickListener { showDeleteConfirmation() }
    }

    private fun fillData() {
        currentExpense?.let {
            binding.etExpenseName.setText(it.title)
            binding.etAmount.setText(it.amount.toString())
            binding.autoCompleteCategory.setText(it.category, false)
            binding.etDate.setText(it.date)
            binding.etNotes.setText(it.description)
        }
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
        return isValid
    }

    //update expenses
    private fun updateExpense() {
        val updatedExpense = currentExpense?.copy(
            title = binding.etExpenseName.text.toString(),
            amount = binding.etAmount.text.toString().toDouble(),
            category = binding.autoCompleteCategory.text.toString(),
            description = binding.etNotes.text.toString(),
            date = binding.etDate.text.toString()
        )
        updatedExpense?.let {
            viewModel.update(it)
            showSnackbar("Expense updated")
            binding.root.postDelayed({ finish() }, 1000)
        }
    }

    //delete expenses
    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                currentExpense?.let {
                    viewModel.delete(it)
                    finish()
                }
            }
            .show()
    }
}
