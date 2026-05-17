package com.madhum.marga.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.madhum.marga.data.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1")
    suspend fun login(email: String, passwordHash: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Int

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}
