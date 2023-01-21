package com.code.korti.seaofimages.presentation.main

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.code.korti.seaofimages.data.SharedPrefsKey
import com.code.korti.seaofimages.data.repository.AuthRepository
import com.code.korti.seaofimages.data.repository.CollectionRepository
import com.code.korti.seaofimages.data.repository.ImageRepository
import com.code.korti.seaofimages.data.repository.Token
import com.code.korti.seaofimages.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationContext = application

    private val authService: AuthorizationService = AuthorizationService(application)
    private val imageRepository = ImageRepository(application)
    private val collectionRepository = CollectionRepository()
    private val authRepository = AuthRepository(application)
    private val successExecutionLiveEvent = SingleLiveEvent<Unit?>()
    private val logoutPageLiveEvent = SingleLiveEvent<Intent>()

    val logoutPageLiveData: LiveData<Intent>
        get() = logoutPageLiveEvent

    val successExecutionLiveData: LiveData<Unit?>
        get() = successExecutionLiveEvent

    fun deleteAllData() {
        viewModelScope.launch {
            imageRepository.deleteAllFromDB()
            collectionRepository.deleteAllCollectionsFromDB()
            authRepository.remoteFromSharedPrefs(SharedPrefsKey.KEY_TOKEN)

            applicationContext.cacheDir.deleteRecursively()
            Token.ACCESS_TOKEN = ""

            successExecutionLiveEvent.postValue(null)
        }
    }

    fun logout() {
        val logoutPageIntent = authService.getEndSessionRequestIntent(
            authRepository.logout()
        )
        logoutPageLiveEvent.postValue(logoutPageIntent)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}