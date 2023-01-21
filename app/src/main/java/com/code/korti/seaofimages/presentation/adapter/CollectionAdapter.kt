package com.code.korti.seaofimages.presentation.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.code.korti.seaofimages.data.models.Collection
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class CollectionAdapter (
    private val listener: CollectionAdapterListener,
    private val context: Context
) : AsyncListDifferDelegationAdapter<Collection>(CollectionDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(CollectionAdapterDelegate(listener, context))
    }

    class CollectionDiffUtilCallback : DiffUtil.ItemCallback<Collection>() {
        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem == newItem
        }
    }
}