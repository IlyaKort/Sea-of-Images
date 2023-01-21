package com.code.korti.seaofimages.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tags(
    val title: String
)
