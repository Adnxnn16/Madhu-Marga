package com.madhum.marga.ui.harvest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.madhum.marga.data.model.YearlyHarvest
import com.madhum.marga.data.db.MadhuDatabase
import com.madhum.marga.data.model.HarvestLog
import com.madhum.marga.data.repository.HarvestRepository
import com.madhum.marga.util.SessionManager
import kotlinx.coroutines.launch

class HarvestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HarvestRepository
    private val sessionManager = SessionManager(application)
    val userId: Int get() = sessionManager.getUserId()

    val allHarvests: LiveData<List<HarvestLog>>
    val totalKg: LiveData<Double?>
    val latestHarvest: LiveData<HarvestLog?>

    private val _yearlyData = MutableLiveData<List<YearlyHarvest>>()
    val yearlyData: LiveData<List<YearlyHarvest>> = _yearlyData

    private val _saveResult = MutableLiveData<Pair<Boolean, String>>()
    val saveResult: LiveData<Pair<Boolean, String>> = _saveResult

    init {
        val db = MadhuDatabase.getDatabase(application)
        repository = HarvestRepository(db.harvestLogDao())
        allHarvests = repository.getAllHarvestsByUser(userId)
        totalKg = repository.getTotalHarvestKg(userId)
        latestHarvest = repository.getLatestHarvestForUser(userId)
        loadYearlyData()
    }

    fun loadYearlyData() {
        viewModelScope.launch {
            val data = repository.getYearlyHarvestSummary(userId)
            _yearlyData.value = data
        }
    }

    fun logHarvest(hiveId: Int, hiveName: String, quantityKg: Double, notes: String, dateMillis: Long) {
        viewModelScope.launch {
            if (quantityKg <= 0) {
                _saveResult.value = Pair(false, "Please enter a valid quantity.")
                return@launch
            }
            val season = repository.getSeason(dateMillis)
            val harvest = HarvestLog(
                hiveId = hiveId,
                hiveName = hiveName,
                harvestDate = dateMillis,
                quantityKg = quantityKg,
                notes = notes,
                season = season
            )
            repository.insertHarvest(harvest)
            loadYearlyData()
            _saveResult.value = Pair(true, "Harvest of ${quantityKg}kg logged for $hiveName!")
        }
    }

    fun deleteHarvest(harvest: HarvestLog) {
        viewModelScope.launch {
            repository.deleteHarvest(harvest)
            loadYearlyData()
        }
    }

    fun getHarvestsForHive(hiveId: Int): LiveData<List<HarvestLog>> =
        repository.getHarvestsByHive(hiveId)
}
