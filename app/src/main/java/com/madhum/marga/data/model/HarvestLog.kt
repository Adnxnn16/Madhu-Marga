package com.madhum.marga.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * HarvestLog entity — records each honey harvest session.
 * FR-06: Harvest Tracker — log honey quantity per hive per session
 * FR-10: Year-over-Year Harvest Comparison chart data
 */
@Entity(
    tableName = "harvest_logs",
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
data class HarvestLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hiveId: Int,
    val hiveName: String = "",           // Denormalized for display
    val harvestDate: Long = System.currentTimeMillis(),
    val quantityKg: Double,             // Honey collected in kilograms
    val qualityRating: Int = 3,         // 1–5 star quality rating
    val notes: String = "",
    val season: String = ""             // Spring / Summer / Monsoon / Winter (auto-calculated)
) {
    /** Derived year for YoY chart grouping */
    fun getYear(): Int {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = harvestDate
        return cal.get(java.util.Calendar.YEAR)
    }
}
