package com.madhum.marga.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.madhum.marga.R
import com.madhum.marga.databinding.ActivityMainBinding
import com.madhum.marga.ui.auth.LoginActivity
import com.madhum.marga.util.AlertNotificationHelper
import com.madhum.marga.util.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Create notification channel
        AlertNotificationHelper.createNotificationChannel(this)

        // Setup navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment
            )
        )

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        setSupportActionBar(binding.toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)

        // Initialize background reminders
        setupBackgroundWorkers()

        // Check notification permission for Android 13+
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupBackgroundWorkers() {
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.madhum.marga.logic.InspectionReminderWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        ).setConstraints(
            androidx.work.Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "InspectionReminder",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun logout() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        sessionManager.logout()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
