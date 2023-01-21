package com.code.korti.seaofimages.presentation.adapter

import android.view.View
import com.code.korti.seaofimages.data.models.Collection

interface CollectionAdapterListener {
    fun onCollectionClicked(cardView: View, collection: Collection)
}