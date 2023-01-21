package com.code.korti.seaofimages.data

import com.code.korti.seaofimages.data.repository.Token
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${Token.ACCESS_TOKEN}")
            .build()

        return chain.proceed(newRequest)
    }
}