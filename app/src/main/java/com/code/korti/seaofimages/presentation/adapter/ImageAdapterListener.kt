package com.code.korti.seaofimages.presentation.adapter

import android.view.View
import com.code.korti.seaofimages.data.models.ImageFromList

interface ImageAdapterListener {
    fun onImageClicked(cardView: View, image: ImageFromList)
}