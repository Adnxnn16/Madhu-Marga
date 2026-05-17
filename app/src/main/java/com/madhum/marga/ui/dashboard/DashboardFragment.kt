package com.madhum.marga.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.madhum.marga.R
import com.madhum.marga.databinding.FragmentDashboardBinding
import com.madhum.marga.logic.DecisionMatrix
import com.madhum.marga.ui.harvest.HarvestViewModel
import com.madhum.marga.ui.hive.HiveViewModel
import com.madhum.marga.ui.inspection.InspectionViewModel
import com.madhum.marga.util.SessionManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * DashboardFragment — overview of all hives at a glance.
 * Redesigned to match accurate dashboard requirement.
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val hiveViewModel: HiveViewModel by activityViewModels()
    private val inspectionViewModel: InspectionViewModel by activityViewModels()
    private val harvestViewModel: HarvestViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        binding.tvWelcome.text = "Welcome, ${session.getFullName().split(" ").firstOrNull() ?: "Admin"}! 🐝"

        setupHoneyFlowBar()
        observeData()
        setupClickListeners()
    }

    private fun setupHoneyFlowBar() {
        val intensity = DecisionMatrix.getHoneyFlowIntensity()
        val season = DecisionMatrix.getCurrentSeason()
        val percent = (intensity * 100).toInt()

        binding.honeyFlowProgress.progress = percent
        binding.tvFlowPercent.text = "$percent%"
        binding.tvFlowSeason.text = "$season Flow"

        binding.tvFlowLabel.text = when {
            intensity >= 0.8f -> "Peak Honey Flow"
            intensity >= 0.5f -> "Moderate Flow"
            intensity >= 0.3f -> "Low Flow"
            else -> "Off Season"
        }
    }

    private fun observeData() {
        hiveViewModel.hiveCount.observe(viewLifecycleOwner) { count ->
            binding.tvHiveCount.text = count.toString()
        }

        inspectionViewModel.latestLog.observe(viewLifecycleOwner) { log ->
            if (log != null) {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                binding.tvLastInspection.text = sdf.format(Date(log.inspectionDate))
                binding.tvLastInspectionHive.text = "Hive: ${log.hiveName}"
            } else {
                binding.tvLastInspection.text = "No inspection yet"
                binding.tvLastInspectionHive.text = "Start inspecting!"
            }
        }

        harvestViewModel.totalKg.observe(viewLifecycleOwner) { total ->
            binding.tvTotalHarvest.text = "%.1f kg".format(total ?: 0.0)
        }

        hiveViewModel.hives.observe(viewLifecycleOwner) { hives ->
            binding.tvHiveHealthSummary.text = if (hives.isEmpty()) {
                "Register your first hive to get started!"
            } else {
                "${hives.size} hive${if (hives.size > 1) "s" else ""} registered. Keep inspecting regularly!"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnQuickInspect.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_inspectionFragment)
        }
        binding.btnLogHarvest.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_harvestFragment)
        }
        binding.btnAllHives.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_hiveListFragment)
        }
        binding.btnViewHives.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_hiveListFragment)
        }
        binding.btnFloraCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_floraCalendarFragment)
        }
        binding.btnLogout.setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Sign Out") { _, _ ->
                    (requireActivity() as? com.madhum.marga.ui.main.MainActivity)?.logout()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
