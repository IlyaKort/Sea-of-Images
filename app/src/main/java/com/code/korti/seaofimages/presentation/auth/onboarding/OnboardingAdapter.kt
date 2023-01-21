package com.code.korti.seaofimages.presentation.auth.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.code.korti.seaofimages.R

class OnboardingAdapter(
    private val onboardingItem: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingItemViewHolder>() {

    inner class OnboardingItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageOnboarding = view.findViewById<ImageView>(R.id.imageOnboarding)
        private val ellipse = view.findViewById<ImageView>(R.id.ellipseOnboarding)
        private val textOnboarding = view.findViewById<TextView>(R.id.textOnboarding)

        fun bind(onboardingItem: OnboardingItem){
            imageOnboarding.setImageResource(onboardingItem.image)
            textOnboarding.setText(onboardingItem.text)

            val ellipseParams = ellipse.layoutParams as ViewGroup.MarginLayoutParams
            val textParams = textOnboarding.layoutParams as ViewGroup.MarginLayoutParams

            when (onboardingItem.number) {
                1 -> {
                    ellipseParams.marginEnd = -50
                    ellipse.layoutParams = ellipseParams

                    textParams.marginStart = 70
                    textOnboarding.layoutParams = textParams
                }
                2 -> {
                    ellipseParams.marginEnd = -200
                    ellipse.layoutParams = ellipseParams
                }
                3 -> {
                    ellipseParams.marginEnd = -450
                    ellipse.layoutParams = ellipseParams

                    textParams.marginEnd = 70
                    textOnboarding.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {
        return OnboardingItemViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_onboarding,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
        holder.bind(onboardingItem[position])
    }

    override fun getItemCount(): Int {
        return onboardingItem.size
    }
}
