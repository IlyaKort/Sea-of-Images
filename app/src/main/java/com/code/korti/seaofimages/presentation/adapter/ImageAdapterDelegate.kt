package com.code.korti.seaofimages.presentation.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.databinding.ItemImageBinding
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import java.util.*
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.ShapeDrawable
import com.code.korti.seaofimages.utils.DrawableColor


class ImageAdapterDelegate(
    private val listener: ImageAdapterListener,
    private val context: Context
) : AbsListItemAdapterDelegate<ImageFromList, ImageFromList, ImageAdapterDelegate.Holder>() {

    override fun isForViewType(
        item: ImageFromList,
        items: MutableList<ImageFromList>,
        position: Int
    ): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        val itemBinding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemBinding, listener, context)
    }

    override fun onBindViewHolder(item: ImageFromList, holder: Holder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    class Holder(
        private val binding: ItemImageBinding,
        listener: ImageAdapterListener,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        private val drawableColor = DrawableColor(context)

        init {
            binding.run {
                this.listener = listener
            }
        }

        fun bind(image: ImageFromList) {
            binding.image = image
            binding.name.text = image.user.name
            binding.userName.text = "@"+image.user.nickname
            binding.likes.text = image.likes.toString()

            val shapeDrawable = ShapeDrawable(RectShape())
            shapeDrawable.intrinsicHeight = image.height
            shapeDrawable.intrinsicWidth = image.width
            shapeDrawable.paint.color = drawableColor.getRandomDrawableColor().color

            if (image.likedByUser){
                binding.favoriteImageView
                    .setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_favorite)
                    )
            }

            val thumbnailRequest = Glide.with(itemView)
                .load(image.urls.thumb)

            Glide.with(itemView)
                .load(image.urls.regular)
                .thumbnail(thumbnailRequest)
                .placeholder(shapeDrawable)
                .into(binding.imageView)

            Glide.with(itemView)
                .load(image.user.profileImage.small)
                .placeholder(R.drawable.ic_account_circle)
                .into(binding.avatar)
        }
    }
}