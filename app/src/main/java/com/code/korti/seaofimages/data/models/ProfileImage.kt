package com.code.korti.seaofimages.data.models

import androidx.room.ColumnInfo
import com.code.korti.seaofimages.data.models.contract.UserContract
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileImage(
    @ColumnInfo(name = UserContract.Urls.SMALL)
    val small: String,
    @ColumnInfo(name = UserContract.Urls.MEDIUM)
    val medium: String,
    @ColumnInfo(name = UserContract.Urls.LARGE)
    val large: String
)
