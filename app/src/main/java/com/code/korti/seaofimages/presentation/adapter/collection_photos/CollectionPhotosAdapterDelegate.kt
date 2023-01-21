package com.code.korti.seaofimages.presentation.adapter.collection_photos

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.CollectionPhotos
import com.code.korti.seaofimages.databinding.ItemCollectionPhotoBinding
import com.code.korti.seaofimages.utils.DrawableColor
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class CollectionPhotosAdapterDelegate(
    private val listener: CollectionPhotosAdapterListener,
    private val context: Context
) :
    AbsListItemAdapterDelegate<CollectionPhotos, CollectionPhotos, CollectionPhotosAdapterDelegate.Holder>() {

    override fun isForViewType(
        item: CollectionPhotos,
        items: MutableList<CollectionPhotos>,
        position: Int
    ): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        val itemBinding =
            ItemCollectionPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemBinding, listener, context)
    }

    override fun onBindViewHolder(
        item: CollectionPhotos,
        holder: Holder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class Holder(
        private val binding: ItemCollectionPhotoBinding,
        listener: CollectionPhotosAdapterListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        private val drawableColor = DrawableColor(context)

        init {
            binding.run {
                this.listener = listener
            }
        }

        fun bind(photo: CollectionPhotos) {
            binding.image = photo
            binding.name.text = photo.user.name
            binding.userName.text = "@" + photo.user.nickname
            binding.likes.text = photo.likes.toString()

            val shapeDrawable = ShapeDrawable(RectShape())
            shapeDrawable.intrinsicHeight = photo.height
            shapeDrawable.intrinsicWidth = photo.width
            shapeDrawable.paint.color = drawableColor.getRandomDrawableColor().color

            if (photo.likedByUser) {
                binding.favoriteImageView
                    .setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_favorite)
                    )
            }

            val thumbnailRequest = Glide.with(itemView)
                .load(photo.urls.thumb)

            Glide.with(itemView)
                .load(photo.urls.regular)
                .thumbnail(thumbnailRequest)
                .placeholder(shapeDrawable)
                .into(binding.imageView)

            Glide.with(itemView)
                .load(photo.user.profileImage.small)
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.avatar)
        }
    }
}