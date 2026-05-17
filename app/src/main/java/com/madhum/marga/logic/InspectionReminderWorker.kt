package com.madhum.marga.logic

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.madhum.marga.data.db.MadhuDatabase
import com.madhum.marga.util.AlertNotificationHelper
import com.madhum.marga.util.SessionManager
import java.util.concurrent.TimeUnit

/**
 * InspectionReminderWorker — background task to check for overdue inspections.
 * FR-12: Push notifications / background reliability.
 */
class InspectionReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sessionManager = SessionManager(applicationContext)
        val userId = sessionManager.getUserId()
        
        if (userId == -1) return Result.success()

        val db = MadhuDatabase.getDatabase(applicationContext)
        val hives = db.hiveDao().getHivesByUserSync(userId)
        
        val currentTime = System.currentTimeMillis()
        val fourteenDaysMillis = TimeUnit.DAYS.toMillis(14)

        for (hive in hives) {
            val logs = db.inspectionLogDao().getLogsByHiveSync(hive.id)
            val lastInspectionTime = if (logs.isNotEmpty()) {
                logs.first().inspectionDate
            } else {
                hive.createdAt
            }

            if (currentTime - lastInspectionTime > fourteenDaysMillis) {
                // Overdue! Send notification
                AlertNotificationHelper.sendInterventionAlert(
                    applicationContext,
                    hive.hiveName,
                    "It's been over 14 days since your last inspection. Check your bees to ensure they are healthy!",
                    hive.id + 2000 // Unique ID for reminder
                )
            }
        }

        return Result.success()
    }
}
