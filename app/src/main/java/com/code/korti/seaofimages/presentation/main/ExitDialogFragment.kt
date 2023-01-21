package com.code.korti.seaofimages.presentation.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.code.korti.seaofimages.databinding.FragmentDialogBinding
import com.code.korti.seaofimages.presentation.auth.AuthActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExitDialogFragment : BottomSheetDialogFragment() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentDialogBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDialogBinding.bind(view)

        binding.noButton.setOnClickListener {
            this.dismiss()
        }

        binding.yesButton.setOnClickListener {
            mainViewModel.logout()
        }

        mainViewModel.successExecutionLiveData.observe(viewLifecycleOwner) {
            val authIntent = Intent(requireActivity(), AuthActivity::class.java)
            requireActivity().finish()
            startActivity(authIntent)
        }

        mainViewModel.logoutPageLiveData.observe(viewLifecycleOwner) {
            logoutResponse.launch(it)
        }
    }

    private val logoutResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            mainViewModel.deleteAllData()
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }
}