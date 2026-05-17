package com.madhum.marga.ui.hive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.madhum.marga.R
import com.madhum.marga.data.model.Hive
import com.madhum.marga.databinding.ItemHiveCardBinding
import java.text.SimpleDateFormat
import java.util.*

class HiveAdapter(
    private val onEditClick: (Hive) -> Unit,
    private val onDeleteClick: (Hive) -> Unit,
    private val onInspectClick: (Hive) -> Unit
) : ListAdapter<Hive, HiveAdapter.HiveViewHolder>(HiveDiffCallback()) {

    inner class HiveViewHolder(private val binding: ItemHiveCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hive: Hive) {
            binding.tvHiveCode.text = hive.hiveCode
            binding.tvHiveName.text = hive.hiveName
            binding.tvHiveType.text = hive.hiveType
            binding.tvHiveLocation.text = "📍 ${hive.location}"

            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvCreatedAt.text = "Added: ${sdf.format(Date(hive.createdAt))}"

            if (hive.notes.isNotBlank()) {
                binding.tvHiveNotes.visibility = android.view.View.VISIBLE
                binding.tvHiveNotes.text = hive.notes
            } else {
                binding.tvHiveNotes.visibility = android.view.View.GONE
            }

            // Hive type icon color
            val iconColor = when (hive.hiveType) {
                "Langstroth" -> R.color.honey_primary
                "Top Bar" -> R.color.honey_accent
                "Log Box" -> R.color.honey_deep
                else -> R.color.honey_primary_dark
            }
            binding.ivHiveIcon.setColorFilter(
                ContextCompat.getColor(binding.root.context, iconColor)
            )

            binding.btnInspect.setOnClickListener { onInspectClick(hive) }
            binding.btnEdit.setOnClickListener { onEditClick(hive) }
            binding.btnDelete.setOnClickListener { onDeleteClick(hive) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HiveViewHolder {
        val binding = ItemHiveCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HiveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HiveViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HiveDiffCallback : DiffUtil.ItemCallback<Hive>() {
        override fun areItemsTheSame(oldItem: Hive, newItem: Hive) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Hive, newItem: Hive) = oldItem == newItem
    }
}
