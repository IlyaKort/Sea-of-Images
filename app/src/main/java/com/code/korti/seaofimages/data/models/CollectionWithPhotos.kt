package com.code.korti.seaofimages.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.code.korti.seaofimages.data.models.contract.CollectionContract
import com.code.korti.seaofimages.data.models.contract.CollectionPhotosContract

data class CollectionWithPhotos(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = CollectionContract.Columns.ID,
        entityColumn = CollectionPhotosContract.Columns.COLLECTION_ID
    )
    val photo: List<CollectionPhotos>
)