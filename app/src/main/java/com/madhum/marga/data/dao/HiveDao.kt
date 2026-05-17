package com.madhum.marga.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.madhum.marga.data.model.Hive

@Dao
interface HiveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHive(hive: Hive): Long

    @Update
    suspend fun updateHive(hive: Hive)

    @Delete
    suspend fun deleteHive(hive: Hive)

    @Query("SELECT * FROM hives WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun getHivesByUser(userId: Int): LiveData<List<Hive>>

    @Query("SELECT * FROM hives WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    suspend fun getHivesByUserSync(userId: Int): List<Hive>

    @Query("SELECT * FROM hives WHERE id = :hiveId LIMIT 1")
    suspend fun getHiveById(hiveId: Int): Hive?

    @Query("SELECT * FROM hives WHERE id = :hiveId LIMIT 1")
    fun getHiveLive(hiveId: Int): LiveData<Hive>

    @Query("SELECT COUNT(*) FROM hives WHERE userId = :userId AND isActive = 1")
    fun getHiveCount(userId: Int): LiveData<Int>

    @Query("UPDATE hives SET isActive = 0, updatedAt = :timestamp WHERE id = :hiveId")
    suspend fun softDeleteHive(hiveId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM hives WHERE userId = :userId AND hiveCode = :hiveCode LIMIT 1")
    suspend fun getHiveByCode(userId: Int, hiveCode: String): Hive?
}
