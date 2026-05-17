package com.madhum.marga.ui.hive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.madhum.marga.R
import com.madhum.marga.databinding.FragmentAddEditHiveBinding

class AddEditHiveFragment : Fragment() {

    private var _binding: FragmentAddEditHiveBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HiveViewModel by activityViewModels()
    private val args: AddEditHiveFragmentArgs by navArgs()

    private var existingHiveId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditHiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        existingHiveId = args.hiveId

        setupHiveTypeSpinner()

        if (existingHiveId != -1) {
            loadExistingHive()
            binding.tvTitle.text = "Edit Hive"
            binding.btnSave.text = "Update Hive"
        }

        binding.btnSave.setOnClickListener { saveHive() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupHiveTypeSpinner() {
        val types = arrayOf("Langstroth", "Top Bar", "Log Box", "Warré")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHiveType.adapter = adapter
    }

    private fun loadExistingHive() {
        viewModel.hives.observe(viewLifecycleOwner) { hives ->
            val hive = hives.firstOrNull { it.id == existingHiveId } ?: return@observe
            binding.etHiveName.setText(hive.hiveName)
            binding.etLocation.setText(hive.location)
            binding.etNotes.setText(hive.notes)

            val types = arrayOf("Langstroth", "Top Bar", "Log Box", "Warré")
            val idx = types.indexOf(hive.hiveType)
            if (idx >= 0) binding.spinnerHiveType.setSelection(idx)
        }
    }

    private fun saveHive() {
        val name = binding.etHiveName.text.toString().trim()
        val type = binding.spinnerHiveType.selectedItem.toString()
        val location = binding.etLocation.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (name.isEmpty()) {
            binding.tilHiveName.error = "Hive name is required"
            return
        }
        if (location.isEmpty()) {
            binding.tilLocation.error = "Location is required"
            return
        }

        binding.tilHiveName.error = null
        binding.tilLocation.error = null

        if (existingHiveId == -1) {
            viewModel.addHive(name, type, location, notes)
        } else {
            viewModel.hives.value?.firstOrNull { it.id == existingHiveId }?.let { hive ->
                viewModel.updateHive(hive, name, type, location, notes)
            }
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { (success, msg) ->
            if (success) {
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
