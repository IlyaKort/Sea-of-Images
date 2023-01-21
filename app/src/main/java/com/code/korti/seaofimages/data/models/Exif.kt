package com.code.korti.seaofimages.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Exif(
    val make: String?,
    val model: String?,
    @Json(name = "exposure_time")
    val exposureTime: String?,
    val aperture: String?,
    @Json(name = "focal_length")
    val focalLength: String?,
    val iso: String?
)
