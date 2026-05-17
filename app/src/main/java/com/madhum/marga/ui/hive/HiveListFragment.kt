package com.madhum.marga.ui.hive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.madhum.marga.R
import com.madhum.marga.databinding.FragmentHiveListBinding
import com.madhum.marga.data.model.Hive

class HiveListFragment : Fragment() {

    private var _binding: FragmentHiveListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HiveViewModel by activityViewModels()
    private lateinit var adapter: HiveAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHiveListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeHives()

        binding.fabAddHive.setOnClickListener {
            findNavController().navigate(R.id.action_hiveListFragment_to_addEditHiveFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = HiveAdapter(
            onEditClick = { hive ->
                val action = HiveListFragmentDirections.actionHiveListFragmentToAddEditHiveFragment(hive.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { hive -> confirmDelete(hive) },
            onInspectClick = { hive ->
                val action = HiveListFragmentDirections.actionHiveListFragmentToInspectionFragment(hive.id, hive.hiveName)
                findNavController().navigate(action)
            }
        )
        binding.recyclerHives.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHives.adapter = adapter
    }

    private fun observeHives() {
        viewModel.hives.observe(viewLifecycleOwner) { hives ->
            if (hives.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.recyclerHives.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.recyclerHives.visibility = View.VISIBLE
                adapter.submitList(hives)
            }
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { (_, msg) ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun confirmDelete(hive: Hive) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Hive")
            .setMessage("Are you sure you want to remove '${hive.hiveName}'? All inspection and harvest data will be deleted.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteHive(hive)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
