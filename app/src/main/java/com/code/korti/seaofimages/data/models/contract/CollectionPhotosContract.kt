package com.code.korti.seaofimages.data.models.contract

object CollectionPhotosContract {

    const val TABLE_NAME = "collection_photos"

    object Columns {
        const val ID = "image_id"
        const val COLLECTION_ID = "photos_collection_id"
        const val USER = "user"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val LIKES = "likes"
        const val LIKED_BY_USER = "likedByUser"
    }
}