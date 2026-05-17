package com.madhum.marga.data.repository

import androidx.lifecycle.LiveData
import com.madhum.marga.data.dao.HarvestLogDao
import com.madhum.marga.data.model.YearlyHarvest
import com.madhum.marga.data.model.HarvestLog

class HarvestRepository(private val dao: HarvestLogDao) {

    fun getHarvestsByHive(hiveId: Int): LiveData<List<HarvestLog>> =
        dao.getHarvestsByHive(hiveId)

    fun getAllHarvestsByUser(userId: Int): LiveData<List<HarvestLog>> =
        dao.getAllHarvestsByUser(userId)

    suspend fun getAllHarvestsByUserSync(userId: Int): List<HarvestLog> =
        dao.getAllHarvestsByUserSync(userId)

    fun getTotalHarvestKg(userId: Int): LiveData<Double?> =
        dao.getTotalHarvestKg(userId)

    fun getLatestHarvestForUser(userId: Int): LiveData<HarvestLog?> =
        dao.getLatestHarvestForUser(userId)

    suspend fun getYearlyHarvestSummary(userId: Int): List<YearlyHarvest> =
        dao.getYearlyHarvestSummary(userId)

    suspend fun insertHarvest(harvest: HarvestLog): Long =
        dao.insertHarvest(harvest)

    suspend fun updateHarvest(harvest: HarvestLog) =
        dao.updateHarvest(harvest)

    suspend fun deleteHarvest(harvest: HarvestLog) =
        dao.deleteHarvest(harvest)

    fun getSeason(timeMillis: Long): String {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = timeMillis }
        return when (cal.get(java.util.Calendar.MONTH) + 1) {
            3, 4, 5 -> "Spring"
            6, 7, 8 -> "Monsoon"
            9, 10, 11 -> "Autumn"
            else -> "Winter"
        }
    }
}
