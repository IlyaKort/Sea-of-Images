package com.code.korti.seaofimages.utils

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemOffsetDecoration(private val context: Context): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val offset = 4.fromDpToPixels(context)
        with(outRect){
            left = offset
            right = offset
            bottom = offset
            top = offset
        }
    }

    private fun Int.fromDpToPixels(context: Context): Int{
        val density = context.resources.displayMetrics.densityDpi
        val pixelsInDp = density / DisplayMetrics.DENSITY_DEFAULT
        return this * pixelsInDp
    }
}