package com.madhum.marga.logic

import com.madhum.marga.data.model.ActivityLevel
import com.madhum.marga.data.model.InspectionLog

/**
 * DecisionMatrix — Rule-based AI engine for Madhu Marga.
 * FR-04: Decision Matrix engine — suggest interventions based on logged data.
 * FR-05: Intervention Alert — trigger notification on "Low Activity" log.
 *
 * Maps 5+ distinct observation combinations to actionable outputs.
 */
object DecisionMatrix {

    enum class AlertType { INTERVENTION, TIP, HEALTHY }

    data class DecisionResult(
        val type: AlertType,
        val title: String,
        val message: String,
        val priority: Int,           // 1 = highest (emergency), 5 = lowest (healthy)
        val shouldNotify: Boolean    // Whether to fire push notification
    )

    /**
     * Analyse an inspection log and return the appropriate decision result.
     * Rules are evaluated in priority order (highest first).
     */
    fun analyse(log: InspectionLog): DecisionResult {
        // ── Rule 1: No Queen + Low Activity → EMERGENCY ───────────────────────
        if (!log.queenPresent && log.activityLevel == ActivityLevel.LOW) {
            return DecisionResult(
                type = AlertType.INTERVENTION,
                title = "🚨 Emergency Alert",
                message = "No queen detected and colony activity is critically low. " +
                          "Consider re-queening or merging this colony immediately. " +
                          "Check for queen cells and inspect all frames carefully.",
                priority = 1,
                shouldNotify = true
            )
        }

        // ── Rule 2: Low Activity → Intervention Alert ─────────────────────────
        if (log.activityLevel == ActivityLevel.LOW) {
            return DecisionResult(
                type = AlertType.INTERVENTION,
                title = "🚨 Intervention Alert",
                message = "Colony activity is critically low. Inspect for queen loss, " +
                          "disease spread, or nutritional stress. Check brood pattern " +
                          "and food stores immediately.",
                priority = 2,
                shouldNotify = true
            )
        }

        // ── Rule 3: Mites/Pests Seen → Treatment Alert ────────────────────────
        if (log.pestsObserved) {
            return DecisionResult(
                type = AlertType.INTERVENTION,
                title = "⚠️ Pest Alert",
                message = "Pests or mites observed in the hive. Apply varroa treatment " +
                          "immediately using approved oxalic acid or thymol-based methods. " +
                          "Recheck and log again in 7 days.",
                priority = 2,
                shouldNotify = true
            )
        }

        // ── Rule 4: High Honey Flow + Full Frames → Harvest Ready ─────────────
        if (log.fullFrames && log.activityLevel == ActivityLevel.HIGH) {
            return DecisionResult(
                type = AlertType.TIP,
                title = "🍯 Harvest Ready!",
                message = "Frames are full and colony activity is high — this is peak " +
                          "harvest time! Add honey supers or extract honey now before " +
                          "bees begin capping. Check moisture content before extracting.",
                priority = 3,
                shouldNotify = false
            )
        }

        // ── Rule 5: High Temperature + Clustering → Ventilation Tip ──────────
        if (log.highTemperature && log.clusteringObserved) {
            return DecisionResult(
                type = AlertType.TIP,
                title = "🌡️ Heat Stress Detected",
                message = "Bees are clustering due to high ambient temperature. " +
                          "Improve hive ventilation by enlarging the entrance. " +
                          "Add shade cover and ensure adequate water source nearby.",
                priority = 3,
                shouldNotify = false
            )
        }

        // ── Rule 6: No Queen but Medium/High Activity → Monitor ───────────────
        if (!log.queenPresent && log.activityLevel != ActivityLevel.LOW) {
            return DecisionResult(
                type = AlertType.TIP,
                title = "👀 Queen Not Spotted",
                message = "Queen was not seen during inspection. Check for eggs and " +
                          "young larvae to confirm her presence. Look for queen cells. " +
                          "Re-inspect in 3 days.",
                priority = 3,
                shouldNotify = false
            )
        }

        // ── Rule 7: Medium Activity → Monitor tip ─────────────────────────────
        if (log.activityLevel == ActivityLevel.MEDIUM) {
            return DecisionResult(
                type = AlertType.TIP,
                title = "📊 Colony Monitoring",
                message = "Colony activity is moderate. Ensure adequate food stores " +
                          "and check for signs of swarming. Regular inspections every " +
                          "7–10 days are recommended during this period.",
                priority = 4,
                shouldNotify = false
            )
        }

        // ── Default: Healthy Colony ────────────────────────────────────────────
        return DecisionResult(
            type = AlertType.HEALTHY,
            title = "✅ Colony is Healthy",
            message = "Your hive is in excellent condition! Queen is present, " +
                      "activity is high, and no pests detected. Continue regular " +
                      "inspections every 10–14 days to maintain this health.",
            priority = 5,
            shouldNotify = false
        )
    }

    /**
     * Returns the current honey flow season intensity (0.0–1.0) based on month.
     * Based on typical Indian beekeeping seasonal calendar.
     */
    fun getHoneyFlowIntensity(): Float {
        val month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        return when (month) {
            11, 12, 1 -> 0.9f  // Nov–Jan: Peak winter flow (mustard, sunflower)
            2, 3       -> 1.0f  // Feb–Mar: Peak spring flow (litchi, mango blossom)
            4, 5       -> 0.6f  // Apr–May: Moderate (eucalyptus, jamun)
            6, 7, 8    -> 0.2f  // Jun–Aug: Monsoon low season
            9, 10      -> 0.5f  // Sep–Oct: Post-monsoon recovery
            else       -> 0.5f
        }
    }

    /**
     * Returns season label based on current month.
     */
    fun getCurrentSeason(): String {
        val month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        return when (month) {
            3, 4, 5 -> "Spring"
            6, 7, 8 -> "Monsoon"
            9, 10, 11 -> "Autumn"
            else -> "Winter"
        }
    }
}
