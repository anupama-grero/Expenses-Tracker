package com.example.expensestracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensestracker.R
import com.example.expensestracker.databinding.ItemExpenseBinding
import com.example.expensestracker.model.Expense
import com.example.expensestracker.utils.AppUtils
import com.example.expensestracker.utils.CurrencyUtils
import com.example.expensestracker.utils.PreferenceManager

class ExpenseAdapter(private val onItemClick: (Expense) -> Unit) :
    ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {

    private var preferenceManager: PreferenceManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        if (preferenceManager == null) {
            preferenceManager = PreferenceManager(parent.context)
        }
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding, preferenceManager!!)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
        holder.itemView.setOnClickListener { onItemClick(expense) }
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_up))
    }

    class ExpenseViewHolder(private val binding: ItemExpenseBinding, private val preferenceManager: PreferenceManager) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            binding.tvExpenseName.text = expense.title
            binding.tvCategory.text = expense.category
            binding.tvDate.text = expense.date
            binding.tvAmount.text = "-${CurrencyUtils.formatCurrency(expense.amount, preferenceManager)}"
            binding.ivCategoryIcon.setImageResource(AppUtils.getCategoryIcon(expense.category))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}
