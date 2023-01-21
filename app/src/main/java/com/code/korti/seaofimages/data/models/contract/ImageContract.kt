package com.code.korti.seaofimages.data.models.contract

object ImageContract {

    const val TABLE_NAME = "image_from_list"

    object Columns {
        const val ID = "image_id"
        const val USER = "user"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val LIKES = "likes"
        const val LIKED_BY_USER = "likedByUser"
    }

    object Urls{
        const val RAW = "image_raw"
        const val FULL = "image_full"
        const val REGULAR = "image_regular"
        const val SMALL = "image_small"
        const val THUMB = "image_thumb"
    }
}