package com.madhum.marga.ui.flora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.madhum.marga.databinding.FragmentFloraCalendarBinding
import com.madhum.marga.logic.FloraData
import java.util.Calendar

/**
 * FloraCalendarFragment — shows Indian blooming flowers by month.
 * FR-07: Flora Calendar — display nearby blooming flowers by month/season.
 * Shows at least 6 months of blooming data (shows all 12).
 */
class FloraCalendarFragment : Fragment() {

    private var _binding: FragmentFloraCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var monthAdapter: FloraMonthAdapter
    private lateinit var flowerAdapter: FlowerAdapter

    private var selectedMonthIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFloraCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) // 0-indexed
        selectedMonthIndex = currentMonth

        setupMonthSelector()
        setupFlowerList()
        displayMonth(selectedMonthIndex)
    }

    private fun setupMonthSelector() {
        monthAdapter = FloraMonthAdapter(selectedMonthIndex) { index ->
            selectedMonthIndex = index
            displayMonth(index)
        }
        binding.recyclerMonths.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        binding.recyclerMonths.adapter = monthAdapter
        binding.recyclerMonths.scrollToPosition(selectedMonthIndex)
    }

    private fun setupFlowerList() {
        flowerAdapter = FlowerAdapter()
        binding.recyclerFlowers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFlowers.adapter = flowerAdapter
    }

    private fun displayMonth(monthIndex: Int) {
        val entry = FloraData.calendar[monthIndex]

        binding.tvMonthName.text = entry.month
        binding.tvSeason.text = "🌸 ${entry.season} Season"
        binding.tvBeekeeperTip.text = "💡 ${entry.beekeeperTip}"

        // Honey flow rating stars
        binding.tvFlowRating.text = "Honey Flow: ${"⭐".repeat(entry.honeyFlowRating)}${"☆".repeat(5 - entry.honeyFlowRating)}"

        flowerAdapter.submitList(entry.flowers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
