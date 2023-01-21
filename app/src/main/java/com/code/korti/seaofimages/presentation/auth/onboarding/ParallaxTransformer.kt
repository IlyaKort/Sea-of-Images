package com.code.korti.seaofimages.presentation.auth.onboarding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.code.korti.seaofimages.R

class ParallaxTransformer(): ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val image = page.findViewById<ImageView>(R.id.imageOnboarding)
        val ellipse = page.findViewById<ImageView>(R.id.ellipseOnboarding)
        val text = page.findViewById<TextView>(R.id.textOnboarding)
        image.translationX = -position * (pageWidth/2)
        ellipse.translationX = position  * (pageWidth/2)
        text.translationX = position * pageWidth
    }
}