package com.madhum.marga.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * SessionManager — manages the logged-in user session using SharedPreferences.
 * Stores userId, email, and name after successful login.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "madhu_marga_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "full_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(userId: Int, email: String, fullName: String) {
        prefs.edit {
            putInt(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, fullName)
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun getFullName(): String = prefs.getString(KEY_NAME, "") ?: ""

    fun logout() {
        prefs.edit { clear() }
    }
}
