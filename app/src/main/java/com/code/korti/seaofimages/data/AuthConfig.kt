package com.code.korti.seaofimages.data

import net.openid.appauth.ResponseTypeValues

object AuthConfig {

    const val AUTH_URI = "https://unsplash.com/oauth/authorize"
    const val TOKEN_URI = "https://unsplash.com/oauth/token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE

    const val CLIENT_ID = "N44x3NkBa6CLsJp7VYAwuX8vSqdXKWS1koYa2-GGDLA"
    const val CLIENT_SECRET = "8vrR7Y6AlpqHdbd11_QEMxv9iRpigZBmbUMGC3pkXhc"
    const val CALLBACK_URL = "seaofimages://seaofimages.com/callback"
    const val END_SESSION_URL = "https://unsplash.com/logout"

    const val SCOPE = "public read_user write_user read_photos write_photos write_likes write_followers read_collections write_collections"
}