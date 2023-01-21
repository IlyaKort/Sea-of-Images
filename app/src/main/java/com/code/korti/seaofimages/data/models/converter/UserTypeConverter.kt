package com.code.korti.seaofimages.data.models.converter

import androidx.room.TypeConverter
import com.code.korti.seaofimages.data.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserTypeConverter {
    private val gson: Gson by lazy { Gson() }

    @TypeConverter
    fun toSource(value: String): User {
        val arrayTutorialType = object : TypeToken<User>() {}.type
        return gson.fromJson(value, arrayTutorialType) as User
    }
    @TypeConverter
    fun fromSource(user: User): String {
        return gson.toJson(user)
    }
}