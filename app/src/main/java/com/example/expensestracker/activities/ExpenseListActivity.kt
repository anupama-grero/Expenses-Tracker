package com.example.expensestracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensestracker.adapter.ExpenseAdapter
import com.example.expensestracker.databinding.ActivityExpenseListBinding
import com.example.expensestracker.model.Expense
import com.example.expensestracker.viewmodel.ExpenseViewModel
import com.google.android.material.chip.Chip

class ExpenseListActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupFilters()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { expense ->
            val intent = Intent(this, EditExpenseActivity::class.java)
            intent.putExtra("EXPENSE", expense)
            startActivity(intent)
        }
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(this@ExpenseListActivity)
            adapter = this@ExpenseListActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.searchView.editText.addTextChangedListener { text ->
            val query = text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchExpenses(query).observe(this) { updateList(it) }
            } else {
                observeData()
            }
        }
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) {
                observeData()
                return@setOnCheckedStateChangeListener
            }
            val chip = findViewById<Chip>(checkedIds.first())
            val category = chip.text.toString()
            if (category == "All") {
                observeData()
            } else {
                viewModel.filterByCategory(category).observe(this) { updateList(it) }
            }
        }
    }

    private fun observeData() {
        viewModel.allExpenses.observe(this) { updateList(it) }
    }

    private fun updateList(expenses: List<Expense>) {
        adapter.submitList(expenses)
        binding.rvExpenses.visibility = if (expenses.isEmpty()) View.GONE else View.VISIBLE
        binding.emptyState.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
    }
}
