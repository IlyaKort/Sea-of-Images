package com.code.korti.seaofimages.presentation.adapter.collection_photos

import android.view.View
import com.code.korti.seaofimages.data.models.CollectionPhotos

interface CollectionPhotosAdapterListener {
    fun onImageClicked(cardView: View, photo: CollectionPhotos)
}