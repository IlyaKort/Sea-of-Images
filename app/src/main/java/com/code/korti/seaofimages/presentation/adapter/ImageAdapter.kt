package com.code.korti.seaofimages.presentation.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.code.korti.seaofimages.data.models.ImageFromList
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class ImageAdapter(
    private val listener: ImageAdapterListener,
    private val context: Context
) : AsyncListDifferDelegationAdapter<ImageFromList>(ImageDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(ImageAdapterDelegate(listener, context))
    }

    class ImageDiffUtilCallback : DiffUtil.ItemCallback<ImageFromList>() {
        override fun areItemsTheSame(oldItem: ImageFromList, newItem: ImageFromList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageFromList, newItem: ImageFromList): Boolean {
            return oldItem == newItem
        }
    }
}