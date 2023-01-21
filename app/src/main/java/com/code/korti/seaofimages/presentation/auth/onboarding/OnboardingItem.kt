package com.code.korti.seaofimages.presentation.auth.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingItem(
    @DrawableRes val image: Int,
    @StringRes val text: Int,
    val number: Int
)
