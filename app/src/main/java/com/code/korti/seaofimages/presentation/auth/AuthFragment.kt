package com.code.korti.seaofimages.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.SharedPrefsKey
import com.code.korti.seaofimages.databinding.FragmentAuthBinding
import com.code.korti.seaofimages.presentation.main.MainActivity
import com.code.korti.seaofimages.utils.launchAndCollectIn
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()
    private val binding: FragmentAuthBinding by viewBinding(FragmentAuthBinding::bind)

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val dataIntent = it.data ?: return@registerForActivityResult
            handleAuthResponseIntent(dataIntent)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()

        if (!viewModel.isSharedPrefs(SharedPrefsKey.KEY_ONBOARDING)) {
            val action = AuthFragmentDirections.actionAuthFragmentToFragmentOnboarding()
            findNavController().navigate(action)
        } else {
            binding.loginButton.setOnClickListener {
                viewModel.openLoginPage()
            }
        }
    }

    private fun bindViewModel() {
        binding.loginButton.setOnClickListener { viewModel.openLoginPage() }
        viewModel.openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner) {
            startForResult.launch(it)
        }
        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            toast(it)
        }
        viewModel.authSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            val mainIntent = Intent(requireContext(), MainActivity::class.java)
            startActivity(mainIntent)
            requireActivity().finish()
        }
    }

    private fun toast(message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        val exception = AuthorizationException.fromIntent(intent)
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            exception != null -> viewModel.onAuthCodeFailed(exception)
            tokenExchangeRequest != null ->
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }
}