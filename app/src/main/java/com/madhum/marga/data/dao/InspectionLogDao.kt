package com.madhum.marga.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.madhum.marga.data.model.InspectionLog

@Dao
interface InspectionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: InspectionLog): Long

    @Update
    suspend fun updateLog(log: InspectionLog)

    @Delete
    suspend fun deleteLog(log: InspectionLog)

    @Query("SELECT * FROM inspection_logs WHERE hiveId = :hiveId ORDER BY inspectionDate DESC")
    fun getLogsByHive(hiveId: Int): LiveData<List<InspectionLog>>

    @Query("SELECT * FROM inspection_logs WHERE hiveId = :hiveId ORDER BY inspectionDate DESC")
    suspend fun getLogsByHiveSync(hiveId: Int): List<InspectionLog>

    @Query("SELECT * FROM inspection_logs ORDER BY inspectionDate DESC LIMIT 1")
    fun getLatestLog(): LiveData<InspectionLog?>

    @Query("SELECT * FROM inspection_logs WHERE hiveId IN (SELECT id FROM hives WHERE userId = :userId) ORDER BY inspectionDate DESC")
    fun getAllLogsByUser(userId: Int): LiveData<List<InspectionLog>>

    @Query("SELECT * FROM inspection_logs WHERE hiveId IN (SELECT id FROM hives WHERE userId = :userId) ORDER BY inspectionDate DESC LIMIT 1")
    fun getLatestLogForUser(userId: Int): LiveData<InspectionLog?>

    @Query("SELECT * FROM inspection_logs WHERE id = :logId LIMIT 1")
    suspend fun getLogById(logId: Int): InspectionLog?

    @Query("SELECT COUNT(*) FROM inspection_logs WHERE hiveId = :hiveId")
    fun getLogCount(hiveId: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM inspection_logs WHERE hiveId IN (SELECT id FROM hives WHERE userId = :userId) AND isAlertTriggered = 1")
    fun getAlertCount(userId: Int): LiveData<Int>
}
