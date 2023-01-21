package com.code.korti.seaofimages.data.models

import androidx.room.Embedded
import androidx.room.Entity
import com.code.korti.seaofimages.data.models.contract.CollectionCoverPhotoContract
import com.squareup.moshi.JsonClass

@Entity(tableName = CollectionCoverPhotoContract.TABLE_NAME)
@JsonClass(generateAdapter = true)
data class CollectionCoverPhoto(
    val id: String,
    val width: Int,
    val height: Int,
    @Embedded
    val urls: ImageUrls
)


