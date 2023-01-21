package com.code.korti.seaofimages.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import com.code.korti.seaofimages.R
import java.util.*

class DrawableColor(context: Context) {
    private val vibrantLightColorList = arrayOf(
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_1)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_2)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_3)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_4)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_5)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_6)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_7)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_8)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_9)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_10)),
        ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_11))
    )

    fun getRandomDrawableColor(): ColorDrawable {
        val idx: Int = Random().nextInt(vibrantLightColorList.size)
        return vibrantLightColorList[idx]
    }
}