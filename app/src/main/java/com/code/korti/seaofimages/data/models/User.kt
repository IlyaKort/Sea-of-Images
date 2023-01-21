package com.code.korti.seaofimages.data.models

import androidx.room.Entity
import androidx.room.TypeConverters
import com.code.korti.seaofimages.data.models.contract.UserContract
import com.code.korti.seaofimages.data.models.converter.ProfileImageTypeConverter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = UserContract.TABLE_NAME)
@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val name: String,
    @Json(name = "username")
    val nickname:String,
    val bio: String?,
    @Json(name = "profile_image")
    val profileImage: ProfileImage
)
