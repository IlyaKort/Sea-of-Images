package com.code.korti.seaofimages.data.models

import androidx.room.*
import com.code.korti.seaofimages.data.models.contract.ImageContract
import com.code.korti.seaofimages.data.models.converter.UserTypeConverter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = ImageContract.TABLE_NAME)
@JsonClass(generateAdapter = true)
data class ImageFromList (
    @PrimaryKey
    @ColumnInfo(name = ImageContract.Columns.ID)
    val id: String,
    @Embedded
    val urls: ImageUrls,
    @ColumnInfo(name = ImageContract.Columns.WIDTH)
    val width: Int,
    @ColumnInfo(name = ImageContract.Columns.HEIGHT)
    val height: Int,
    @ColumnInfo(name = ImageContract.Columns.LIKES)
    val likes: Int,
    @ColumnInfo(name = ImageContract.Columns.LIKED_BY_USER)
    @Json(name = "liked_by_user")
    val likedByUser: Boolean,
    @field:TypeConverters(UserTypeConverter::class)
    val user: User
)