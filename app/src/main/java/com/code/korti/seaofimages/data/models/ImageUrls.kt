package com.code.korti.seaofimages.data.models

import androidx.room.ColumnInfo
import com.code.korti.seaofimages.data.models.contract.ImageContract
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageUrls(
    @ColumnInfo(name = ImageContract.Urls.RAW)
    val raw: String,
    @ColumnInfo(name = ImageContract.Urls.FULL)
    val full: String,
    @ColumnInfo(name = ImageContract.Urls.REGULAR)
    val regular: String,
    @ColumnInfo(name = ImageContract.Urls.SMALL)
    val small: String,
    @ColumnInfo(name = ImageContract.Urls.THUMB)
    val thumb: String,
)
