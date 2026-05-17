package com.madhum.marga.ui.flora

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madhum.marga.databinding.ItemFlowerCardBinding
import com.madhum.marga.logic.FloraData

class FlowerAdapter : ListAdapter<FloraData.FlowerInfo, FlowerAdapter.FlowerViewHolder>(FlowerDiff()) {

    inner class FlowerViewHolder(private val binding: ItemFlowerCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(flower: FloraData.FlowerInfo) {
            binding.tvFlowerName.text = flower.name
            binding.tvLocalName.text = flower.localName
            binding.tvRegion.text = "📍 ${flower.region}"
            binding.tvYield.text = "Yield: ${flower.honeyYield}"

            val yieldColor = when (flower.honeyYield) {
                "High" -> Color.parseColor("#16A34A")
                "Medium" -> Color.parseColor("#F59E0B")
                else -> Color.parseColor("#6B7280")
            }
            binding.tvYield.setTextColor(yieldColor)

            try {
                binding.viewColorDot.setBackgroundColor(Color.parseColor(flower.color))
            } catch (e: Exception) {
                binding.viewColorDot.setBackgroundColor(Color.parseColor("#F59E0B"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val binding = ItemFlowerCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlowerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) = holder.bind(getItem(position))

    class FlowerDiff : DiffUtil.ItemCallback<FloraData.FlowerInfo>() {
        override fun areItemsTheSame(old: FloraData.FlowerInfo, new: FloraData.FlowerInfo) = old.name == new.name
        override fun areContentsTheSame(old: FloraData.FlowerInfo, new: FloraData.FlowerInfo) = old == new
    }
}

class FloraMonthAdapter(
    private var selectedIndex: Int,
    private val onMonthClick: (Int) -> Unit
) : RecyclerView.Adapter<FloraMonthAdapter.MonthViewHolder>() {

    private val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    inner class MonthViewHolder(private val binding: com.madhum.marga.databinding.ItemMonthChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(month: String, index: Int, isSelected: Boolean) {
            binding.tvMonth.text = month
            if (isSelected) {
                binding.root.setCardBackgroundColor(Color.parseColor("#F59E0B"))
                binding.tvMonth.setTextColor(Color.WHITE)
            } else {
                binding.root.setCardBackgroundColor(Color.parseColor("#FEF3C7"))
                binding.tvMonth.setTextColor(Color.parseColor("#92400E"))
            }
            binding.root.setOnClickListener {
                val prev = selectedIndex
                selectedIndex = index
                notifyItemChanged(prev)
                notifyItemChanged(index)
                onMonthClick(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val binding = com.madhum.marga.databinding.ItemMonthChipBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MonthViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(months[position], position, position == selectedIndex)
    }

    override fun getItemCount() = months.size
}
