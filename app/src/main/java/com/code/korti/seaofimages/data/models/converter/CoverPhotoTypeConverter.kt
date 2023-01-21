package com.code.korti.seaofimages.data.models.converter

import androidx.room.TypeConverter
import com.code.korti.seaofimages.data.models.CollectionCoverPhoto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CoverPhotoTypeConverter {
    private val gson: Gson by lazy { Gson() }

    @TypeConverter
    fun toSource(value: String): CollectionCoverPhoto {
        val arrayTutorialType = object : TypeToken<CollectionCoverPhoto>() {}.type
        return gson.fromJson(value, arrayTutorialType) as CollectionCoverPhoto
    }
    @TypeConverter
    fun fromSource(collectionCoverPhoto: CollectionCoverPhoto): String {
        return gson.toJson(collectionCoverPhoto)
    }
}