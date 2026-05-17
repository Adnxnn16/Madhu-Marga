package com.madhum.marga.ui.hive

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.madhum.marga.data.db.MadhuDatabase
import com.madhum.marga.data.model.Hive
import com.madhum.marga.data.repository.HiveRepository
import com.madhum.marga.util.SessionManager
import kotlinx.coroutines.launch

class HiveViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HiveRepository
    private val sessionManager = SessionManager(application)
    val userId: Int get() = sessionManager.getUserId()

    val hives: LiveData<List<Hive>>
    val hiveCount: LiveData<Int>

    private val _operationResult = MutableLiveData<Pair<Boolean, String>>()
    val operationResult: LiveData<Pair<Boolean, String>> = _operationResult

    init {
        val db = MadhuDatabase.getDatabase(application)
        repository = HiveRepository(db.hiveDao())
        hives = repository.getHivesByUser(userId)
        hiveCount = repository.getHiveCount(userId)
    }

    fun getHivesSync(callback: (List<Hive>) -> Unit) {
        viewModelScope.launch {
            val list = repository.getHivesByUserSync(userId)
            callback(list)
        }
    }

    fun addHive(name: String, type: String, location: String, notes: String) {
        viewModelScope.launch {
            val existingCount = repository.getHivesByUserSync(userId).size
            val code = repository.generateHiveCode(existingCount)
            val hive = Hive(
                userId = userId,
                hiveCode = code,
                hiveName = name,
                hiveType = type,
                location = location,
                notes = notes
            )
            val id = repository.insertHive(hive)
            if (id > 0) {
                _operationResult.value = Pair(true, "Hive '$name' ($code) registered successfully!")
            } else {
                _operationResult.value = Pair(false, "Failed to register hive.")
            }
        }
    }

    fun updateHive(hive: Hive, name: String, type: String, location: String, notes: String) {
        viewModelScope.launch {
            val updated = hive.copy(
                hiveName = name,
                hiveType = type,
                location = location,
                notes = notes,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateHive(updated)
            _operationResult.value = Pair(true, "Hive updated successfully!")
        }
    }

    fun deleteHive(hive: Hive) {
        viewModelScope.launch {
            repository.softDeleteHive(hive.id)
            _operationResult.value = Pair(true, "Hive '${hive.hiveName}' removed.")
        }
    }
}
