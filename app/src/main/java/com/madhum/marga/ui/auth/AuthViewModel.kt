package com.madhum.marga.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.madhum.marga.data.db.MadhuDatabase
import com.madhum.marga.data.model.User
import com.madhum.marga.util.PasswordUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

/**
 * AuthViewModel — handles registration and login logic via Firebase.
 * Synchronizes with local Room DB for offline-first support.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = MadhuDatabase.getDatabase(application)
    private val userDao = db.userDao()

    val authResult = MutableLiveData<AuthResult>()

    fun register(email: String, password: String, fullName: String, mobile: String) {
        viewModelScope.launch {
            authResult.value = AuthResult.Loading

            if (!PasswordUtils.isValidEmail(email)) {
                authResult.value = AuthResult.Error("Please enter a valid email address.")
                return@launch
            }
            if (!PasswordUtils.isValidPassword(password)) {
                authResult.value = AuthResult.Error("Password must be at least 6 characters.")
                return@launch
            }
            if (fullName.isBlank()) {
                authResult.value = AuthResult.Error("Please enter your full name.")
                return@launch
            }

            try {
                // 1. Firebase Registration
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // 2. Sync with Local Room DB
                    val localUser = User(
                        email = email.lowercase().trim(),
                        passwordHash = "firebase_managed", // We don't store plain passwords
                        fullName = fullName.trim(),
                        mobileNumber = mobile.trim()
                    )
                    val insertedId = userDao.insertUser(localUser)
                    
                    authResult.value = AuthResult.Success(localUser.copy(id = insertedId.toInt()))
                } else {
                    authResult.value = AuthResult.Error("Firebase registration failed.")
                }
            } catch (e: Exception) {
                authResult.value = AuthResult.Error(e.localizedMessage ?: "Registration failed.")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authResult.value = AuthResult.Loading

            if (!PasswordUtils.isValidEmail(email)) {
                authResult.value = AuthResult.Error("Please enter a valid email address.")
                return@launch
            }
            if (password.isBlank()) {
                authResult.value = AuthResult.Error("Please enter your password.")
                return@launch
            }

            try {
                // 1. Firebase Login
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // 2. Get local user or create if missing (sync)
                    var localUser = userDao.getUserByEmail(email.lowercase().trim())
                    
                    if (localUser == null) {
                        // This might happen if they cleared data but Firebase session persists
                        localUser = User(
                            email = email.lowercase().trim(),
                            passwordHash = "firebase_managed",
                            fullName = firebaseUser.displayName ?: "Beekeeper"
                        )
                        val id = userDao.insertUser(localUser)
                        localUser = localUser.copy(id = id.toInt())
                    }
                    
                    authResult.value = AuthResult.Success(localUser)
                } else {
                    authResult.value = AuthResult.Error("Invalid email or password.")
                }
            } catch (e: Exception) {
                authResult.value = AuthResult.Error(e.localizedMessage ?: "Login failed.")
            }
        }
    }
}
