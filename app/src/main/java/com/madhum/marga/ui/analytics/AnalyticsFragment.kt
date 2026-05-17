package com.madhum.marga.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.madhum.marga.R
import com.madhum.marga.databinding.FragmentAnalyticsBinding
import com.madhum.marga.ui.harvest.HarvestViewModel

/**
 * AnalyticsFragment — visualises Year-over-Year harvest performance.
 * FR-10: Year-over-Year Harvest Chart.
 */
class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HarvestViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        observeData()
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setPinchZoom(false)
            setScaleEnabled(false)
            legend.isEnabled = true

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.DKGRAY
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                textColor = Color.DKGRAY
            }

            axisRight.isEnabled = false
            animateY(1000)
        }
    }

    private fun observeData() {
        viewModel.yearlyData.observe(viewLifecycleOwner) { data ->
            if (data.isNullOrEmpty()) {
                binding.cardChart.visibility = View.GONE
                binding.statsContainer.visibility = View.GONE
                binding.tvEmptyState.visibility = View.VISIBLE
                return@observe
            }

            binding.cardChart.visibility = View.VISIBLE
            binding.statsContainer.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE

            val entries = data.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.totalKg.toFloat())
            }

            val dataSet = BarDataSet(entries, "Honey Production (kg)").apply {
                color = Color.parseColor("#F59E0B") // honey_primary
                valueTextColor = Color.BLACK
                valueTextSize = 12f
            }

            val barData = BarData(dataSet)
            barData.barWidth = 0.6f

            binding.barChart.apply {
                this.data = barData
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index >= 0 && index < data.size) {
                            data[index].year
                        } else ""
                    }
                }
                invalidate()
            }
        }

        viewModel.totalKg.observe(viewLifecycleOwner) { total ->
            binding.tvTotalYield.text = "%.1f kg".format(total ?: 0.0)
        }

        // Simple heuristic for best season based on recent logs (could be more complex)
        viewModel.allHarvests.observe(viewLifecycleOwner) { harvests ->
            if (!harvests.isNullOrEmpty()) {
                val bestSeason = harvests.groupBy { it.season }
                    .maxByOrNull { it.value.sumOf { h -> h.quantityKg } }
                    ?.key ?: "Spring"
                binding.tvBestSeason.text = bestSeason
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
