package com.code.korti.seaofimages.data.repository

import android.content.Context
import android.net.Uri
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.AuthConfig
import com.code.korti.seaofimages.data.SharedPrefsKey
import com.code.korti.seaofimages.presentation.auth.onboarding.OnboardingItem
import net.openid.appauth.*
import timber.log.Timber
import kotlin.coroutines.suspendCoroutine


class AuthRepository(
    context: Context
) {

    val onboardingItems: List<OnboardingItem> = listOf(
        OnboardingItem(
            image = R.drawable.onboarding_1,
            text = R.string.onbourding_text_1,
            1
        ),
        OnboardingItem(
            image = R.drawable.onboarding_2,
            text = R.string.onbourding_text_2,
            2
        ),
        OnboardingItem(
            image = R.drawable.onboarding_3,
            text = R.string.onbourding_text_3,
            3
        )
    )

    private val sharedPrefs by lazy {
        context.getSharedPreferences(SharedPrefsKey.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isSharedPrefs(key: String): Boolean {
        return sharedPrefs.contains(key)
    }

    private val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_URI),
        Uri.parse(AuthConfig.TOKEN_URI),
        null,
        Uri.parse(AuthConfig.END_SESSION_URL)
    )

    fun getAuthRequest(): AuthorizationRequest {
        val redirectUri = Uri.parse(AuthConfig.CALLBACK_URL)

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScope(AuthConfig.SCOPE)
            .setCodeVerifier(null)
            .build()
    }

    suspend fun performTokenRequestSuspend(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): Token {
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(tokenRequest, getClientAuthentication()) { response, ex ->
                when {
                    response != null -> {

                        Timber.d("response = $response")

                        sharedPrefs.edit()
                            .putString(SharedPrefsKey.KEY_TOKEN, response.accessToken.orEmpty())
                            .commit()

                        Token.ACCESS_TOKEN = response.accessToken.orEmpty()

                        continuation.resumeWith(Result.success(Token))
                    }
                    ex != null -> {
                        Timber.e("ex = $ex")
                        continuation.resumeWith(Result.failure(ex))
                    }
                    else -> error("unreachable")
                }
            }
        }
    }

    fun getSharedPrefs(key: String): String? {
        return sharedPrefs.getString(key, null)
    }

    fun setSharedPrefs(key: String, value: String){
        sharedPrefs.edit()
            .putString(key, value)
            .commit()
    }

    fun remoteFromSharedPrefs(key: String){
        sharedPrefs.edit().remove(key).commit()
    }

    fun logout(): EndSessionRequest {
        return EndSessionRequest.Builder(serviceConfiguration)
            .build()
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }
}

object Token {
    var ACCESS_TOKEN = "token"
}