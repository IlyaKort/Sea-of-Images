package com.code.korti.seaofimages.presentation.adapter

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.Collection
import com.code.korti.seaofimages.databinding.ItemCollectionBinding
import com.code.korti.seaofimages.utils.DrawableColor
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class CollectionAdapterDelegate (
    private val listener: CollectionAdapterListener,
    private val context: Context
) :
    AbsListItemAdapterDelegate<Collection, Collection, CollectionAdapterDelegate.Holder>() {

    override fun isForViewType(
        item: Collection,
        items: MutableList<Collection>,
        position: Int
    ): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        val itemBinding =
            ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemBinding, listener, context)
    }

    override fun onBindViewHolder(item: Collection, holder: Holder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    class Holder(
        private val binding: ItemCollectionBinding,
        listener: CollectionAdapterListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        private val drawableColor = DrawableColor(context)

        init {
            binding.run {
                this.listener = listener
            }
        }

        fun bind(collection: Collection) {
            binding.collection = collection

            binding.totalPhotosTextView.text = collection.totalPhotos.toString() + " Photo"
            binding.titleTextView.text = collection.title

            binding.name.text = collection.user.name
            binding.userName.text = "@" + collection.user.nickname

            val shapeDrawable = ShapeDrawable(RectShape())
            shapeDrawable.intrinsicHeight = collection.coverPhoto.height
            shapeDrawable.intrinsicWidth = collection.coverPhoto.width
            shapeDrawable.paint.color = drawableColor.getRandomDrawableColor().color

            Glide.with(itemView)
                .load(collection.coverPhoto.urls.thumb)
                .placeholder(shapeDrawable)
                .into(binding.imageView)

            Glide.with(itemView)
                .load(collection.user.profileImage.small)
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.avatar)
        }
    }
}