package com.code.korti.seaofimages.presentation.detail

import android.app.Application
import androidx.lifecycle.*
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.repository.ImageRepository
import com.code.korti.seaofimages.data.models.Image
import com.code.korti.seaofimages.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ImageRepository(application)

    private val imageLiveData = MutableLiveData<Image>()
    private val isLoadingImageLiveData = MutableLiveData<Boolean>()
    private val permissionsGrantedMutableLiveData = MutableLiveData<Boolean>()
    private val errorLiveEvent = SingleLiveEvent<Int>()
    private val successLiveEvent = SingleLiveEvent<String>()

    val successLiveData: LiveData<String>
        get() = successLiveEvent

    val errorLiveData: LiveData<Int>
        get() = errorLiveEvent

    val image: LiveData<Image>
        get() = imageLiveData

    val isLoadingImage: LiveData<Boolean>
        get() = isLoadingImageLiveData

    fun downloadImage(url: String) {
        if (repository.isConnected()) {
            repository.startDownload(url)
        } else {
            errorLiveEvent.postValue(R.string.no_internet_connection)
        }
    }

    fun trackPhotoDownload(imageId: String) {
        viewModelScope.launch {
            try {
                if (repository.isConnected()) {
                    repository.trackPhotoDownload(imageId)
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (t: Throwable) {
                Timber.d(t)
            }
        }
    }

    fun bind(id: String) {
        viewModelScope.launch {
            try {
                if (repository.isConnected()) {
                    isLoadingImageLiveData.postValue(true)
                    imageLiveData.postValue(repository.getImage(id))
                    successLiveEvent.postValue("")
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (e: Throwable) {
                Timber.d(e)
                errorLiveEvent.postValue(R.string.no_more_requests)
            } finally {
                isLoadingImageLiveData.postValue(false)
            }
        }
    }

    fun putLike(id: String) {
        viewModelScope.launch {
            try {
                if (repository.isConnected()) {
                    repository.putLike(id)
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (e: Throwable) {
                errorLiveEvent.postValue(R.string.no_more_requests)
            }
        }
    }

    fun deleteLike(id: String) {
        viewModelScope.launch {
            try {
                if (repository.isConnected()) {
                    repository.deleteLike(id)
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (e: Throwable) {
                Timber.d(e)
                errorLiveEvent.postValue(R.string.no_more_requests)
            }
        }
    }

    //Permission
    fun permissionsGranted(url: String) {
        downloadImage(url)
        permissionsGrantedMutableLiveData.postValue(true)
    }

    fun permissionsDenied() {
        permissionsGrantedMutableLiveData.postValue(false)
    }
}