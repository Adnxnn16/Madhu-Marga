package com.madhum.marga.util

import java.security.MessageDigest

/**
 * Password hashing utility using SHA-256.
 * Used for local authentication without Firebase.
 */
object PasswordUtils {

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return hashPassword(password) == hash
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}
