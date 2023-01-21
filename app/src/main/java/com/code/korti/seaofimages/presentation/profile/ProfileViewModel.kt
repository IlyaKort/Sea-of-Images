package com.code.korti.seaofimages.presentation.profile

import android.app.Application
import androidx.lifecycle.*
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.data.models.Profile
import com.code.korti.seaofimages.data.repository.ProfileRepository
import com.code.korti.seaofimages.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)

    private val profileLiveData = MutableLiveData<Profile>()
    private val imageListLiveData = MutableLiveData<List<ImageFromList>>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val errorLiveEvent = SingleLiveEvent<Int>()

    val errorLiveData: LiveData<Int>
        get() = errorLiveEvent

    val imageList: LiveData<List<ImageFromList>>
        get() = imageListLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    val profile: LiveData<Profile>
        get() = profileLiveData

    fun loadPage(username: String, page: Int, numberOfImages: Int) {
        viewModelScope.launch {
            try {
                if (repository.isConnected()) {
                    isLoadingLiveData.postValue(true)
                    val list = repository.getImagesList(username, page, numberOfImages)
                    imageListLiveData.postValue(list)
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (e: Throwable) {
                Timber.d(e)
                errorLiveEvent.postValue(R.string.no_more_requests)
            } finally {
                isLoadingLiveData.postValue(false)
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                if (repository.isConnected()) {
                    profileLiveData.postValue(repository.getMyProfile())
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (e: Throwable) {
                Timber.d(e)
                errorLiveEvent.postValue(R.string.no_more_requests)
            }
        }
    }
}