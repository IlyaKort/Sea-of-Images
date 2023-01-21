package com.code.korti.seaofimages.data.models.converter

import androidx.room.TypeConverter
import com.code.korti.seaofimages.data.models.ProfileImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileImageTypeConverter {
    private val gson: Gson by lazy { Gson() }

    @TypeConverter
    fun toSource(value: String): ProfileImage {
        val arrayTutorialType = object : TypeToken<ProfileImage>() {}.type
        return gson.fromJson(value, arrayTutorialType) as ProfileImage
    }
    @TypeConverter
    fun fromSource(profileImage: ProfileImage): String {
        return gson.toJson(profileImage)
    }
}