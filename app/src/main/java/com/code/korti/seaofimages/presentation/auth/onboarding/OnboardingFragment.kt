package com.code.korti.seaofimages.presentation.auth.onboarding

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.SharedPrefsKey
import com.code.korti.seaofimages.databinding.FragmentOnboardingBinding
import com.code.korti.seaofimages.presentation.auth.AuthViewModel

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val viewModel: AuthViewModel by viewModels()

    private val binding: FragmentOnboardingBinding by viewBinding(FragmentOnboardingBinding::bind)
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var indicatorContainer: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOnboarding()
        setupIndicators()
        setCurrentIndicator(0)

        binding.nextButton.setOnClickListener {
            openAuthFragment()
        }

        binding.skipTextView.setOnClickListener {
            openAuthFragment()
        }
    }

    private fun openAuthFragment() {
        viewModel.setSharedPrefs(SharedPrefsKey.KEY_ONBOARDING, "yes")
        val action = OnboardingFragmentDirections.actionOnboardingFragmentToAuthFragment()
        findNavController().navigate(action)
    }

    private fun initOnboarding() {
        onboardingAdapter = OnboardingAdapter(viewModel.onboardingItems)
        binding.viewPager.adapter = onboardingAdapter

        binding.viewPager.setPageTransformer(ParallaxTransformer())

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

                if (position == 2) {
                    binding.nextButton.visibility = View.VISIBLE
                } else {
                    binding.nextButton.visibility = View.GONE
                }
            }
        })
    }

    private fun setupIndicators() {
        indicatorContainer = binding.indicatorContainer
        val indicators = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_acrive_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive_background
                    )
                )
            }
        }
    }
}