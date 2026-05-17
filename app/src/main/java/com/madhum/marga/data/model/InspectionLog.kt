package com.madhum.marga.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Activity level enum for inspection logs.
 */
enum class ActivityLevel { HIGH, MEDIUM, LOW }

/**
 * InspectionLog entity — structured checklist per hive visit.
 * FR-03: Inspection Log — checklist (Queen present, pests, activity level)
 */
@Entity(
    tableName = "inspection_logs",
    foreignKeys = [
        ForeignKey(
            entity = Hive::class,
            parentColumns = ["id"],
            childColumns = ["hiveId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hiveId")]
)
data class InspectionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hiveId: Int,
    val hiveName: String = "",            // Denormalized for display
    val inspectionDate: Long = System.currentTimeMillis(),
    val queenPresent: Boolean = true,     // Queen seen during inspection?
    val pestsObserved: Boolean = false,   // Mites / pests observed?
    val activityLevel: ActivityLevel = ActivityLevel.HIGH,  // HIGH / MEDIUM / LOW
    val clusteringObserved: Boolean = false,  // Bees clustering (heat stress)?
    val fullFrames: Boolean = false,      // Honey frames full? (harvest readiness)
    val highTemperature: Boolean = false, // High ambient temperature noted?
    val notes: String = "",
    val aiTip: String = "",              // Generated tip from Decision Matrix
    val isAlertTriggered: Boolean = false // Was an intervention alert triggered?
)
