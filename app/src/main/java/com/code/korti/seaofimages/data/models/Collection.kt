package com.code.korti.seaofimages.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.code.korti.seaofimages.data.models.contract.CollectionContract
import com.code.korti.seaofimages.data.models.converter.CoverPhotoTypeConverter
import com.code.korti.seaofimages.data.models.converter.UserTypeConverter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = CollectionContract.TABLE_NAME)
@JsonClass(generateAdapter = true)
data class Collection(
    @PrimaryKey
    @ColumnInfo(name = CollectionContract.Columns.ID)
    val id: String,
    val title: String,
    val description: String?,
    @ColumnInfo(name = CollectionContract.Columns.TOTAL_PHOTOS)
    @Json(name = "total_photos")
    val totalPhotos: Int,
    @field:TypeConverters(CoverPhotoTypeConverter::class)
    @ColumnInfo(name = CollectionContract.Columns.COVER_PHOTOS)
    @Json(name = "cover_photo")
    val coverPhoto: CollectionCoverPhoto,
    @field:TypeConverters(UserTypeConverter::class)
    val user: User
)
