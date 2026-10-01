package com.example.expensestracker.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.example.expensestracker.R
import com.example.expensestracker.databinding.ActivitySettingsBinding
import com.example.expensestracker.viewmodel.ExpenseViewModel
import com.example.expensestracker.utils.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        setupThemeToggle()
        setupCurrencyPreference()
        setupDataManagement()
    }

    private fun setupThemeToggle() {
        val currentMode = preferenceManager.getThemeMode()
        binding.themeToggleGroup.check(if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) R.id.btnDarkTheme else R.id.btnLightTheme)

        binding.themeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val newMode = when (checkedId) {
                    R.id.btnLightTheme -> AppCompatDelegate.MODE_NIGHT_NO
                    R.id.btnDarkTheme -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                if (newMode != preferenceManager.getThemeMode()) {
                    preferenceManager.setThemeMode(newMode)
                }
            }
        }
    }

    private fun setupCurrencyPreference() {
        when (preferenceManager.getCurrency()) {
            "LKR" -> binding.chipGroupCurrency.check(R.id.chipLKR)
            "USD" -> binding.chipGroupCurrency.check(R.id.chipUSD)
            "EUR" -> binding.chipGroupCurrency.check(R.id.chipEUR)
        }

        binding.chipGroupCurrency.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedCurrency = when (checkedIds.first()) {
                    R.id.chipLKR -> "LKR"
                    R.id.chipUSD -> "USD"
                    R.id.chipEUR -> "EUR"
                    else -> "USD"
                }
                preferenceManager.setCurrency(selectedCurrency)
                showSnackbar("Currency changed to $selectedCurrency")
            }
        }
    }

    private fun setupDataManagement() {
        binding.btnExport.setOnClickListener { showSnackbar("Exporting data...") }
        binding.btnResetData.setOnClickListener { showResetConfirmation() }
    }

    private fun showResetConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.reset_app_data_title)
            .setMessage(R.string.reset_app_data_message)
            .setPositiveButton(R.string.reset) { _, _ ->
                viewModel.resetAllData(
                    onSuccess = {
                        showSnackbar(getString(R.string.reset_success), true)
                    },
                    onError = {
                        showSnackbar(getString(R.string.reset_error), true)
                    }
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
