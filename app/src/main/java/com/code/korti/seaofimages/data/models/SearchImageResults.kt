package com.code.korti.seaofimages.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchImageResults(
    @Json(name = "results")
    val images: List<ImageFromList>?
)