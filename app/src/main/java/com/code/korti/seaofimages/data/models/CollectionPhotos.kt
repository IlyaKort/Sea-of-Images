package com.code.korti.seaofimages.data.models

import androidx.room.*
import com.code.korti.seaofimages.data.models.contract.CollectionPhotosContract
import com.code.korti.seaofimages.data.models.converter.UserTypeConverter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = CollectionPhotosContract.TABLE_NAME)
data class CollectionPhotos(
    @PrimaryKey
    @ColumnInfo(name = CollectionPhotosContract.Columns.ID)
    val id: String,
    @Embedded
    val urls: ImageUrls,
    @ColumnInfo(name = CollectionPhotosContract.Columns.WIDTH)
    val width: Int,
    @ColumnInfo(name = CollectionPhotosContract.Columns.HEIGHT)
    val height: Int,
    @ColumnInfo(name = CollectionPhotosContract.Columns.LIKES)
    val likes: Int,
    @ColumnInfo(name = CollectionPhotosContract.Columns.LIKED_BY_USER)
    val likedByUser: Boolean,
    @ColumnInfo(name = CollectionPhotosContract.Columns.USER)
    @field:TypeConverters(UserTypeConverter::class)
    val user: User,
    @ColumnInfo(name = CollectionPhotosContract.Columns.COLLECTION_ID)
    val collectionId: String,
)