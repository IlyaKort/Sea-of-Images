package com.code.korti.seaofimages.data.models.contract

object UserContract {

    const val TABLE_NAME = "user"

    object Columns {
        const val ID = "user_id"
        const val NAME = "name"
        const val NICKNAME = "nickname"
        const val BIO = "bio"
    }
    object Urls{
        const val SMALL = "user_avatar_small"
        const val MEDIUM = "user_avatar_medium"
        const val LARGE = "user_avatar_large"
    }
}