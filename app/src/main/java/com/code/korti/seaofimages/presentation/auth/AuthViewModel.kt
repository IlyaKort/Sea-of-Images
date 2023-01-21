package com.code.korti.seaofimages.presentation.auth

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import timber.log.Timber

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)
    private val authService: AuthorizationService = AuthorizationService(application)

    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val authSuccessFlow: Flow<Unit>
        get() = authSuccessEventChannel.receiveAsFlow()

    fun onAuthCodeFailed(exception: AuthorizationException) {
        toastEventChannel.trySendBlocking(R.string.auth_canceled)
    }

    val onboardingItems = authRepository.onboardingItems

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            runCatching {
                authRepository.performTokenRequestSuspend(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                authSuccessEventChannel.send(Unit)
            }.onFailure {
                Timber.e("throwable = $it")
                toastEventChannel.send(R.string.auth_canceled)
            }
        }
    }

    fun openLoginPage() {
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRepository.getAuthRequest()
        )
        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)
    }

    fun isSharedPrefs(key: String): Boolean {
        return authRepository.isSharedPrefs(key)
    }

    fun getSharedPrefs(key: String): String? {
        return authRepository.getSharedPrefs(key)
    }

    fun setSharedPrefs(key: String, value: String) {
        authRepository.setSharedPrefs(key, value)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}