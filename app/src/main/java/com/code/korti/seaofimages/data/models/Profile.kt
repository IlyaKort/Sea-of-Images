package com.code.korti.seaofimages.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Profile(
    val id: String,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String,
    @Json(name = "username")
    val nickname: String,
    val bio: String?,
    @Json(name = "portfolio_url")
    val profileUrls: ProfileImage?,
    val location: String?
)
