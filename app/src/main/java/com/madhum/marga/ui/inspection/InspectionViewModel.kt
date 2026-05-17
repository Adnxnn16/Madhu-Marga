package com.madhum.marga.ui.inspection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.madhum.marga.data.db.MadhuDatabase
import com.madhum.marga.data.model.ActivityLevel
import com.madhum.marga.data.model.InspectionLog
import com.madhum.marga.data.repository.InspectionRepository
import com.madhum.marga.logic.DecisionMatrix
import com.madhum.marga.util.AlertNotificationHelper
import com.madhum.marga.util.SessionManager
import kotlinx.coroutines.launch

class InspectionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InspectionRepository
    private val sessionManager = SessionManager(application)
    val userId: Int get() = sessionManager.getUserId()

    val allLogsForUser: LiveData<List<InspectionLog>>
    val latestLog: LiveData<InspectionLog?>
    val alertCount: LiveData<Int>

    private val _decisionResult = MutableLiveData<DecisionMatrix.DecisionResult?>()
    val decisionResult: LiveData<DecisionMatrix.DecisionResult?> = _decisionResult

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    init {
        val db = MadhuDatabase.getDatabase(application)
        repository = InspectionRepository(db.inspectionLogDao())
        allLogsForUser = repository.getAllLogsByUser(userId)
        latestLog = repository.getLatestLogForUser(userId)
        alertCount = repository.getAlertCount(userId)
    }

    fun getLogsForHive(hiveId: Int): LiveData<List<InspectionLog>> =
        repository.getLogsByHive(hiveId)

    fun saveInspection(
        hiveId: Int,
        hiveName: String,
        queenPresent: Boolean,
        pestsObserved: Boolean,
        activityLevel: ActivityLevel,
        clusteringObserved: Boolean,
        fullFrames: Boolean,
        highTemperature: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            val tempLog = InspectionLog(
                hiveId = hiveId,
                hiveName = hiveName,
                queenPresent = queenPresent,
                pestsObserved = pestsObserved,
                activityLevel = activityLevel,
                clusteringObserved = clusteringObserved,
                fullFrames = fullFrames,
                highTemperature = highTemperature,
                notes = notes
            )

            // Run decision matrix
            val result = DecisionMatrix.analyse(tempLog)
            _decisionResult.value = result

            // Save final log with AI tip embedded
            val logToSave = tempLog.copy(
                aiTip = result.message,
                isAlertTriggered = result.shouldNotify
            )
            repository.insertLog(logToSave)

            // Fire notification if needed
            if (result.shouldNotify) {
                AlertNotificationHelper.sendInterventionAlert(
                    getApplication(),
                    hiveName,
                    result.message,
                    hiveId + 1000
                )
            }

            _saveResult.value = true
        }
    }

    fun clearDecisionResult() {
        _decisionResult.value = null
    }
}
