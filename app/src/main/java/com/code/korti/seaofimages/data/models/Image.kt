package com.code.korti.seaofimages.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    val id: String,
    val urls: ImageUrls,
    val likes: Int,
    val downloads: Int,
    val width: Int,
    val height: Int,
    @Json(name = "liked_by_user")
    val likedByUser: Boolean,
    val exif: Exif,
    val location: Location,
    val tags: List<Tags>,
    val user: User
)
