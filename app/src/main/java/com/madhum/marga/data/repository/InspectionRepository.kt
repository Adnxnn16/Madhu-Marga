package com.madhum.marga.data.repository

import androidx.lifecycle.LiveData
import com.madhum.marga.data.dao.InspectionLogDao
import com.madhum.marga.data.model.InspectionLog

class InspectionRepository(private val dao: InspectionLogDao) {

    fun getLogsByHive(hiveId: Int): LiveData<List<InspectionLog>> =
        dao.getLogsByHive(hiveId)

    suspend fun getLogsByHiveSync(hiveId: Int): List<InspectionLog> =
        dao.getLogsByHiveSync(hiveId)

    fun getAllLogsByUser(userId: Int): LiveData<List<InspectionLog>> =
        dao.getAllLogsByUser(userId)

    fun getLatestLogForUser(userId: Int): LiveData<InspectionLog?> =
        dao.getLatestLogForUser(userId)

    fun getAlertCount(userId: Int): LiveData<Int> =
        dao.getAlertCount(userId)

    suspend fun insertLog(log: InspectionLog): Long =
        dao.insertLog(log)

    suspend fun updateLog(log: InspectionLog) =
        dao.updateLog(log)

    suspend fun deleteLog(log: InspectionLog) =
        dao.deleteLog(log)
}
