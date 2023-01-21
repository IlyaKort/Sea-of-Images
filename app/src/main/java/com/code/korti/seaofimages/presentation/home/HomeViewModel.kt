package com.code.korti.seaofimages.presentation.home

import android.app.Application
import androidx.lifecycle.*
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.repository.ImageRepository
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ImageRepository(application)

    private val imageListLiveData = MutableLiveData<List<ImageFromList>>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val errorLiveEvent = SingleLiveEvent<Int>()

    val errorLiveData: LiveData<Int>
        get() = errorLiveEvent

    val imageList: LiveData<List<ImageFromList>>
        get() = imageListLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun loadPage(page: Int, numberOfImages: Int) {
        viewModelScope.launch {
            try {
                if (repository.isConnected()) {
                    isLoadingLiveData.postValue(true)
                    val list = repository.getImagesList(page, numberOfImages)
                    imageListLiveData.postValue(list)
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (e: Throwable) {
                imageListLiveData.postValue(repository.getImagesFromDB())
                errorLiveEvent.postValue(R.string.no_more_requests_db)
                Timber.d(e)
            } finally {
                isLoadingLiveData.postValue(false)
            }
        }
    }
}