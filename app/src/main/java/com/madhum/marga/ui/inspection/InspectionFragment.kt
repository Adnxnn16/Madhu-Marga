package com.madhum.marga.ui.inspection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.madhum.marga.R
import com.madhum.marga.data.model.ActivityLevel
import com.madhum.marga.databinding.FragmentInspectionBinding
import com.madhum.marga.logic.DecisionMatrix
import com.madhum.marga.ui.hive.HiveViewModel

/**
 * InspectionFragment — structured checklist for hive inspection.
 * FR-03: Inspection Log checklist.
 * FR-04: Decision Matrix.
 * FR-05: Intervention Alert.
 */
class InspectionFragment : Fragment() {

    private var _binding: FragmentInspectionBinding? = null
    private val binding get() = _binding!!

    private val inspectionViewModel: InspectionViewModel by activityViewModels()
    private val hiveViewModel: HiveViewModel by activityViewModels()
    private val args: InspectionFragmentArgs by navArgs()

    private var selectedHiveId: Int = -1
    private var selectedHiveName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInspectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-select hive if navigated from hive detail
        if (args.hiveId != -1) {
            selectedHiveId = args.hiveId
            selectedHiveName = args.hiveName
        }

        setupHiveSelector()
        setupActivityLevelButtons()
        observeResults()

        binding.btnSaveInspection.setOnClickListener { saveInspection() }
    }

    private fun setupHiveSelector() {
        hiveViewModel.hives.observe(viewLifecycleOwner) { hives ->
            if (hives.isEmpty()) {
                binding.tvNoHivesWarning.visibility = View.VISIBLE
                binding.btnSaveInspection.isEnabled = false
                return@observe
            }
            binding.tvNoHivesWarning.visibility = View.GONE
            binding.btnSaveInspection.isEnabled = true

            val hiveNames = hives.map { "${it.hiveCode} — ${it.hiveName}" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hiveNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerHive.adapter = adapter

            // Pre-select if coming from hive detail
            if (selectedHiveId != -1) {
                val idx = hives.indexOfFirst { it.id == selectedHiveId }
                if (idx >= 0) binding.spinnerHive.setSelection(idx)
            }

            binding.spinnerHive.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    selectedHiveId = hives[pos].id
                    selectedHiveName = hives[pos].hiveName
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }

            if (selectedHiveId == -1 && hives.isNotEmpty()) {
                selectedHiveId = hives[0].id
                selectedHiveName = hives[0].hiveName
            }
        }
    }

    private var selectedActivity = ActivityLevel.HIGH

    private fun setupActivityLevelButtons() {
        updateActivityButtons(ActivityLevel.HIGH)

        binding.btnActivityHigh.setOnClickListener {
            selectedActivity = ActivityLevel.HIGH
            updateActivityButtons(ActivityLevel.HIGH)
        }
        binding.btnActivityMedium.setOnClickListener {
            selectedActivity = ActivityLevel.MEDIUM
            updateActivityButtons(ActivityLevel.MEDIUM)
        }
        binding.btnActivityLow.setOnClickListener {
            selectedActivity = ActivityLevel.LOW
            updateActivityButtons(ActivityLevel.LOW)
        }
    }

    private fun updateActivityButtons(level: ActivityLevel) {
        val selected = ContextCompat.getColor(requireContext(), R.color.honey_primary)
        val unselected = ContextCompat.getColor(requireContext(), R.color.divider)
        val textSelected = ContextCompat.getColor(requireContext(), R.color.text_on_primary)
        val textUnselected = ContextCompat.getColor(requireContext(), R.color.text_primary)

        listOf(
            binding.btnActivityHigh to ActivityLevel.HIGH,
            binding.btnActivityMedium to ActivityLevel.MEDIUM,
            binding.btnActivityLow to ActivityLevel.LOW
        ).forEach { (btn, btnLevel) ->
            val isSelected = btnLevel == level
            btn.setBackgroundColor(if (isSelected) selected else unselected)
            btn.setTextColor(if (isSelected) textSelected else textUnselected)
        }
    }

    private fun saveInspection() {
        if (selectedHiveId == -1) {
            binding.tvNoHivesWarning.visibility = View.VISIBLE
            return
        }

        binding.btnSaveInspection.isEnabled = false
        binding.progressSaving.visibility = View.VISIBLE

        inspectionViewModel.saveInspection(
            hiveId = selectedHiveId,
            hiveName = selectedHiveName,
            queenPresent = binding.switchQueenPresent.isChecked,
            pestsObserved = binding.switchPestsSeen.isChecked,
            activityLevel = selectedActivity,
            clusteringObserved = binding.switchClustering.isChecked,
            fullFrames = binding.switchFullFrames.isChecked,
            highTemperature = binding.switchHighTemp.isChecked,
            notes = binding.etNotes.text.toString().trim()
        )
    }

    private fun observeResults() {
        inspectionViewModel.decisionResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            binding.btnSaveInspection.isEnabled = true
            binding.progressSaving.visibility = View.GONE
            showResultDialog(result)
            inspectionViewModel.clearDecisionResult()
        }
    }

    private fun showResultDialog(result: DecisionMatrix.DecisionResult) {
        val iconRes = when (result.type) {
            DecisionMatrix.AlertType.INTERVENTION -> R.drawable.ic_alert
            DecisionMatrix.AlertType.TIP -> R.drawable.ic_tip
            DecisionMatrix.AlertType.HEALTHY -> R.drawable.ic_healthy
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(result.title)
            .setMessage(result.message)
            .setIcon(iconRes)
            .setPositiveButton("Got it") { dialog, _ ->
                dialog.dismiss()
                // Reset form
                resetForm()
            }
            .setCancelable(false)
            .show()
    }

    private fun resetForm() {
        binding.switchQueenPresent.isChecked = true
        binding.switchPestsSeen.isChecked = false
        binding.switchClustering.isChecked = false
        binding.switchFullFrames.isChecked = false
        binding.switchHighTemp.isChecked = false
        binding.etNotes.text?.clear()
        selectedActivity = ActivityLevel.HIGH
        updateActivityButtons(ActivityLevel.HIGH)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
