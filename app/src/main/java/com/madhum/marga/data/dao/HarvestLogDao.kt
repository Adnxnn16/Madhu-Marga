package com.madhum.marga.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.madhum.marga.data.model.HarvestLog
import com.madhum.marga.data.model.YearlyHarvest

@Dao
interface HarvestLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvest(harvest: HarvestLog): Long

    @Update
    suspend fun updateHarvest(harvest: HarvestLog)

    @Delete
    suspend fun deleteHarvest(harvest: HarvestLog)

    @Query("SELECT * FROM harvest_logs WHERE hiveId = :hiveId ORDER BY harvestDate DESC")
    fun getHarvestsByHive(hiveId: Int): LiveData<List<HarvestLog>>

    @Query("SELECT * FROM harvest_logs WHERE hiveId IN (SELECT id FROM hives WHERE userId = :userId) ORDER BY harvestDate DESC")
    fun getAllHarvestsByUser(userId: Int): LiveData<List<HarvestLog>>

    @Query("SELECT * FROM harvest_logs WHERE hiveId IN (SELECT id FROM hives WHERE userId = :userId) ORDER BY harvestDate DESC")
    suspend fun getAllHarvestsByUserSync(userId: Int): List<HarvestLog>

    @Query("SELECT SUM(quantityKg) FROM harvest_logs WHERE hiveId IN (SELECT id FROM hives WHERE userId = :userId)")
    fun getTotalHarvestKg(userId: Int): LiveData<Double?>

    @Query("SELECT SUM(quantityKg) FROM harvest_logs WHERE hiveId = :hiveId")
    fun getTotalByHive(hiveId: Int): LiveData<Double?>

    @Query("SELECT * FROM harvest_logs WHERE hiveId IN (SELECT id FROM hives WHERE userId = :userId) ORDER BY harvestDate DESC LIMIT 1")
    fun getLatestHarvestForUser(userId: Int): LiveData<HarvestLog?>

    @Query("""
        SELECT strftime('%Y', harvestDate / 1000, 'unixepoch') as year, 
               SUM(quantityKg) as totalKg 
        FROM harvest_logs 
        WHERE hiveId IN (SELECT id FROM hives WHERE userId = :userId)
        GROUP BY year 
        ORDER BY year DESC
        LIMIT 5
    """)
    suspend fun getYearlyHarvestSummary(userId: Int): List<YearlyHarvest>
}
