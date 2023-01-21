package com.code.korti.seaofimages.data.models.contract


object CollectionContract {

    const val TABLE_NAME = "collection"

    object Columns {
        const val ID = "collection_id"
        const val TITLE = "title"
        const val TOTAL_PHOTOS = "total_photos"
        const val COVER_PHOTOS = "cover_photo"
        const val USER = "user"
    }
}