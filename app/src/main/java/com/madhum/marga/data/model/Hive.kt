package com.madhum.marga.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Hive entity — represents a single beehive registered by the user.
 * FR-02: Create / Edit Hive Profile (ID, location, hive type)
 * FR-11: Hive Register — tag hives with unique ID and GPS/text location
 */
@Entity(tableName = "hives")
data class Hive(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,                        // Linked to local User
    val hiveCode: String,                   // Unique human-readable hive ID e.g. "HV-001"
    val hiveName: String,                   // Descriptive name e.g. "Garden Hive"
    val hiveType: String,                   // Langstroth / Top Bar / Log Box / Warré
    val location: String,                   // Text location or GPS coordinates
    val notes: String = "",
    val imageUri: String = "",              // Optional image URI
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
