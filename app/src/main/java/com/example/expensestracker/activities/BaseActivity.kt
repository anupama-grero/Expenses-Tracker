package com.example.expensestracker.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.example.expensestracker.utils.PreferenceManager
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity() {

    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        preferenceManager = PreferenceManager(this)
        // Apply theme mode before super.onCreate to avoid flickering
        AppCompatDelegate.setDefaultNightMode(preferenceManager.getThemeMode())
        super.onCreate(savedInstanceState)
    }

    protected fun setupToolbar(toolbar: Toolbar, title: String? = null, showBackButton: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            this.title = title ?: toolbar.title
            setDisplayHomeAsUpEnabled(showBackButton)
        }
        if (showBackButton) {
            toolbar.setNavigationOnClickListener { finish() }
        }
    }

    protected fun showSnackbar(message: String, isLong: Boolean = false) {
        val duration = if (isLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT
        Snackbar.make(findViewById(android.R.id.content), message, duration).show()
    }
}
