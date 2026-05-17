package com.madhum.marga.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for local authentication.
 * Replaces Firebase Auth — all auth is handled locally via Room DB + SHA-256 password hashing.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val passwordHash: String,         // SHA-256 hashed password
    val fullName: String,
    val mobileNumber: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
