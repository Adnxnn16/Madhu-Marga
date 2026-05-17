package com.madhum.marga.ui.harvest

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.snackbar.Snackbar
import com.madhum.marga.R
import com.madhum.marga.databinding.FragmentHarvestBinding
import com.madhum.marga.ui.hive.HiveViewModel
import com.madhum.marga.data.model.YearlyHarvest
import java.text.SimpleDateFormat
import java.util.*

/**
 * HarvestFragment — log honey harvest + Year-over-Year bar chart.
 * FR-06: Harvest Tracker.
 * FR-10: Year-over-Year Harvest Comparison chart.
 */
class HarvestFragment : Fragment() {

    private var _binding: FragmentHarvestBinding? = null
    private val binding get() = _binding!!

    private val harvestViewModel: HarvestViewModel by activityViewModels()
    private val hiveViewModel: HiveViewModel by activityViewModels()

    private var selectedHiveId: Int = -1
    private var selectedHiveName: String = ""
    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHarvestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDateDisplay()
        setupHiveSelector()
        setupDatePicker()
        setupChart()
        observeData()

        binding.btnLogHarvest.setOnClickListener { logHarvest() }
    }

    private fun setupHiveSelector() {
        hiveViewModel.hives.observe(viewLifecycleOwner) { hives ->
            if (hives.isEmpty()) return@observe
            val names = hives.map { "${it.hiveCode} — ${it.hiveName}" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerHive.adapter = adapter

            binding.spinnerHive.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    selectedHiveId = hives[pos].id
                    selectedHiveName = hives[pos].hiveName
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }

            if (hives.isNotEmpty()) {
                selectedHiveId = hives[0].id
                selectedHiveName = hives[0].hiveName
            }
        }
    }

    private fun setupDatePicker() {
        binding.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val pickedCal = Calendar.getInstance()
                    pickedCal.set(year, month, day)
                    selectedDateMillis = pickedCal.timeInMillis
                    updateDateDisplay()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.tvSelectedDate.text = sdf.format(Date(selectedDateMillis))
    }

    private fun logHarvest() {
        val quantityStr = binding.etQuantity.text.toString()
        val quantity = quantityStr.toDoubleOrNull()

        if (quantity == null || quantity <= 0) {
            binding.tilQuantity.error = "Enter a valid quantity (e.g. 2.5)"
            return
        }
        binding.tilQuantity.error = null

        if (selectedHiveId == -1) {
            Snackbar.make(binding.root, "Please select a hive first.", Snackbar.LENGTH_SHORT).show()
            return
        }

        harvestViewModel.logHarvest(
            hiveId = selectedHiveId,
            hiveName = selectedHiveName,
            quantityKg = quantity,
            notes = binding.etNotes.text.toString().trim(),
            dateMillis = selectedDateMillis
        )

        binding.etQuantity.text?.clear()
        binding.etNotes.text?.clear()
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setDrawGridBackground(false)
            setDrawBorders(false)
            animateY(800)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textSize = 12f
                textColor = Color.parseColor("#1C1917")
            }

            axisLeft.apply {
                granularity = 1f
                axisMinimum = 0f
                textSize = 12f
                textColor = Color.parseColor("#1C1917")
            }

            axisRight.isEnabled = false
        }
    }

    private fun updateChart(data: List<YearlyHarvest>) {
        if (data.isEmpty()) {
            binding.tvChartEmpty.visibility = View.VISIBLE
            binding.barChart.visibility = View.GONE
            return
        }

        binding.tvChartEmpty.visibility = View.GONE
        binding.barChart.visibility = View.VISIBLE

        val reversed = data.reversed()
        val entries = reversed.mapIndexed { idx, item ->
            BarEntry(idx.toFloat(), item.totalKg.toFloat())
        }

        val dataSet = BarDataSet(entries, "Honey Yield (kg)").apply {
            color = Color.parseColor("#F59E0B")
            valueTextColor = Color.parseColor("#1C1917")
            valueTextSize = 12f
            setDrawValues(true)
        }

        val labels = reversed.map { it.year }
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.data = BarData(dataSet).apply { barWidth = 0.6f }
        binding.barChart.invalidate()
    }

    private fun observeData() {
        harvestViewModel.totalKg.observe(viewLifecycleOwner) { total ->
            binding.tvTotalKg.text = "Total: %.1f kg".format(total ?: 0.0)
        }

        harvestViewModel.allHarvests.observe(viewLifecycleOwner) { harvests ->
            if (harvests.isEmpty()) {
                binding.layoutNoHarvest.visibility = View.VISIBLE
            } else {
                binding.layoutNoHarvest.visibility = View.GONE
            }
        }

        harvestViewModel.yearlyData.observe(viewLifecycleOwner) { yearlyData ->
            updateChart(yearlyData)
        }

        harvestViewModel.saveResult.observe(viewLifecycleOwner) { (_, msg) ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
