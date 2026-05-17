package com.madhum.marga.data.repository

import androidx.lifecycle.LiveData
import com.madhum.marga.data.dao.HiveDao
import com.madhum.marga.data.model.Hive

class HiveRepository(private val hiveDao: HiveDao) {

    fun getHivesByUser(userId: Int): LiveData<List<Hive>> =
        hiveDao.getHivesByUser(userId)

    suspend fun getHivesByUserSync(userId: Int): List<Hive> =
        hiveDao.getHivesByUserSync(userId)

    fun getHiveCount(userId: Int): LiveData<Int> =
        hiveDao.getHiveCount(userId)

    fun getHiveLive(hiveId: Int): LiveData<Hive> =
        hiveDao.getHiveLive(hiveId)

    suspend fun getHiveById(hiveId: Int): Hive? =
        hiveDao.getHiveById(hiveId)

    suspend fun insertHive(hive: Hive): Long =
        hiveDao.insertHive(hive)

    suspend fun updateHive(hive: Hive) =
        hiveDao.updateHive(hive)

    suspend fun deleteHive(hive: Hive) =
        hiveDao.deleteHive(hive)

    suspend fun softDeleteHive(hiveId: Int) =
        hiveDao.softDeleteHive(hiveId)

    suspend fun hiveCodeExists(userId: Int, hiveCode: String): Boolean =
        hiveDao.getHiveByCode(userId, hiveCode) != null

    fun generateHiveCode(existingCount: Int): String =
        "HV-%03d".format(existingCount + 1)
}
