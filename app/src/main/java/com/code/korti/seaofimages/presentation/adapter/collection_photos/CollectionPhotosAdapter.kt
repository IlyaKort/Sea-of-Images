package com.code.korti.seaofimages.presentation.adapter.collection_photos

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.code.korti.seaofimages.data.models.CollectionPhotos
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class CollectionPhotosAdapter(
    private val listener: CollectionPhotosAdapterListener,
    private val context: Context
) : AsyncListDifferDelegationAdapter<CollectionPhotos>(CollectionPhotosDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(CollectionPhotosAdapterDelegate(listener, context))
    }

    class CollectionPhotosDiffUtilCallback : DiffUtil.ItemCallback<CollectionPhotos>() {
        override fun areItemsTheSame(oldItem: CollectionPhotos, newItem: CollectionPhotos): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CollectionPhotos, newItem: CollectionPhotos): Boolean {
            return oldItem == newItem
        }
    }
}